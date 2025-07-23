package com.logmaster.task;

import com.google.gson.JsonObject;
import com.logmaster.LogMasterConfig;
import com.logmaster.domain.SaveData;
import com.logmaster.domain.Task;
import com.logmaster.domain.TaskTier;
import com.logmaster.domain.TieredTaskList;
import com.logmaster.util.FileUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.logmaster.util.GsonOverride.GSON;

@Singleton
@Slf4j
public class TaskService {

    private static final String DEF_FILE_TASKS = "task-list.json";

    @Inject
    private LogMasterConfig config;

    @Inject
    private TaskListClient taskListClient;

    private TieredTaskList localList;
    private TieredTaskList remoteList;
    private boolean requestedRemoteList = false;

    public TieredTaskList getTaskList() {
        if (localList == null) {
            this.localList = FileUtils.loadDefinitionResource(TieredTaskList.class, DEF_FILE_TASKS);
        }
        if (remoteList == null && !requestedRemoteList && config.loadRemoteTaskList()) {
            loadRemoteTaskList();
        }
        return remoteList != null && config.loadRemoteTaskList() ? remoteList : localList;
    }

    public List<Task> getForTier(TaskTier tier) {
        return getTaskList().getForTier(tier);
    }

    public Map<TaskTier, Integer> completionPercentages(SaveData saveData) {
        Map<TaskTier, Set<String>> progressData = saveData.getProgress();
        TieredTaskList taskList = getTaskList();

        Map<TaskTier, Integer> completionPercentages = new HashMap<>();
        for (TaskTier tier : TaskTier.values()) {
            Set<String> tierCompletedTasks = new HashSet<>(progressData.get(tier));
            Set<String> tierTaskIdList = taskList.getForTier(tier)
                    .stream()
                    .map(Task::getId)
                    .collect(Collectors.toSet());

            tierCompletedTasks.retainAll(tierTaskIdList);

            double tierPercentage = 100d * tierCompletedTasks.size() / tierTaskIdList.size();

            completionPercentages.put(tier, (int) Math.floor(tierPercentage));
        }

        return completionPercentages;
    }

    private void loadRemoteTaskList() {
        requestedRemoteList = true;
        // Load the remote task list
        try {
            taskListClient.getTaskList(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    log.error("Unable to load remote task list, will defer to the default task list", e);
                    requestedRemoteList = false;
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    JsonObject tasksJson = taskListClient.processResponse(response);
                    response.close();
                    if (tasksJson == null) {
                        log.error("Loaded null remote task list, will defer to the default task list");
                        return;
                    }
                    log.debug("Loaded remote task list!");
                    remoteList = GSON.fromJson(tasksJson, TieredTaskList.class);
                }
            });
        } catch (IOException e) {
            log.error("Unable to load remote task list, will defer to the default task list");
            this.localList = FileUtils.loadDefinitionResource(TieredTaskList.class, DEF_FILE_TASKS);
        }
    }
}
