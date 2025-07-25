package com.logmaster.persistence;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.logmaster.domain.*;
import com.logmaster.domain.old.OldSaveData;
import com.logmaster.domain.old.OldTask;
import com.logmaster.domain.old.OldTaskPointer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import static com.logmaster.LogMasterConfig.*;
import static com.logmaster.util.GsonOverride.GSON;

@Singleton
@Slf4j
public class SaveDataManager {
    @Inject
    private ConfigManager configManager;

    private SaveData saveData;

    public @NonNull SaveData getSaveData() {
        this.saveData = loadSaveData();
        return this.saveData;
    }

    public void save() {
        String json = GSON.toJson(this.saveData);
        this.configManager.setRSProfileConfiguration(CONFIG_GROUP, SAVE_DATA_KEY, json);
    }

    public Task currentTask() {
        TaskPointer activeTaskPointer = getSaveData().getActiveTaskPointer();
        return activeTaskPointer != null ? activeTaskPointer.getTask() : null;
    }

    private SaveData loadSaveData() {
        String json = this.configManager.getRSProfileConfiguration(CONFIG_GROUP, SAVE_DATA_KEY);
        if (json == null) {
            return new SaveData();
        }

        try {
            BaseSaveData base = GSON.fromJson(json, BaseSaveData.class);
            if (BaseSaveData.LATEST_VERSION.equals(base.getVersion())) {
                return GSON.fromJson(json, SaveData.class);
            }

            return this.update(json);
        } catch (JsonSyntaxException e) {
            log.error("Unable to parse save data JSON", e);
        }

        return new SaveData();
    }

    @SuppressWarnings("deprecation")
    private SaveData update(String json) {
        SaveData updated = new SaveData();

        OldSaveData old = null;
        try {
            old = GSON.fromJson(json, OldSaveData.class);
        } catch (JsonSyntaxException e) {
            log.error("Unable to parse *old* save data JSON", e);
        }

        if (old == null) {
            return updated;
        }

        Type mapType = new TypeToken<Map<TaskTier, Map<Integer, String>>>() {}.getType();
        Map<TaskTier, Map<Integer, String>> v0MigrationData;
        try (InputStream resourceStream = this.getClass().getResourceAsStream("v0-migration.json")) {
            assert resourceStream != null;
            InputStreamReader definitionReader = new InputStreamReader(resourceStream);
            v0MigrationData = GSON.fromJson(definitionReader, mapType);
        } catch (IOException e) {
            log.error("Unable to parse migration data", e);
            return updated;
        }

        Map<TaskTier, Set<Integer>> oldProgress = old.getProgress();
        Map<TaskTier, Set<String>> newProgress = updated.getProgress();

        for (TaskTier tier : TaskTier.values()) {
            Set<Integer> oldTierData = oldProgress.get(tier);
            Set<String> newTierData = newProgress.get(tier);
            Map<Integer, String> tierMigrationData = v0MigrationData.get(tier);

            for (Integer oldTaskId : oldTierData) {
                if (tierMigrationData.containsKey(oldTaskId)) {
                    newTierData.add(tierMigrationData.get(oldTaskId));
                }
            }
        }

        updated.setSelectedTier(old.getSelectedTier());

        OldTaskPointer oldTaskPointer = old.getActiveTaskPointer();
        if (oldTaskPointer != null) {
            OldTask oldTask = oldTaskPointer.getTask();
            String newTaskId = v0MigrationData.get(oldTaskPointer.getTaskTier()).get(oldTask.getId());
            Task newTask = new Task(newTaskId, oldTask.getDescription(), oldTask.getItemID(), null);
            updated.setActiveTaskPointer(new TaskPointer(oldTaskPointer.getTaskTier(), newTask));
        }

        return updated;
    }
}
