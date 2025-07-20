package com.logmaster.clog;

import com.logmaster.LogMasterPlugin;
import com.logmaster.diary.AchievementDiaryManager;
import com.logmaster.domain.Task;
import com.logmaster.domain.TaskTier;
import com.logmaster.domain.verification.CollectionLogVerification;
import com.logmaster.task.TaskService;
import com.logmaster.ui.InterfaceManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.EnumComposition;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.client.callback.ClientThread;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
public class ClogItemsManager {

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LogMasterPlugin plugin;

    @Inject
    private TaskService taskService;

    @Inject
    private InterfaceManager interfaceManager;

    @Inject
    private AchievementDiaryManager achievementDiaryManager;

    private static final HashSet<Integer> clogItemsUnlocked = new HashSet<>();
    private final Object syncButtonLock = new Object();
    private Timer syncButtonTimer = new java.util.Timer("SyncButtonTimer", true);
    private TimerTask syncButtonTask = null;
    private boolean userInitiatedSync = false;

    public boolean isCollectionLogItemUnlocked(int itemId) {
        // Some items have bad IDs, check these ones for a replacement
        EnumComposition replacements = client.getEnum(3721);
        int replacementItemId = replacements.getIntValue(itemId);
        itemId = replacementItemId >= 0 ? replacementItemId : itemId;

        // Check if the bit is set in our bitset
        boolean isUnlocked = clogItemsUnlocked.contains(itemId);
        return isUnlocked;
    }

    private void scheduleSync() {
        synchronized (syncButtonLock) {
            if (syncButtonTask != null) {
                syncButtonTask.cancel();
            }
            syncButtonTask = new java.util.TimerTask() {
                @Override
                public void run() {
                    clientThread.invokeLater(() -> syncClogWithProgress());
                }
            };
            syncButtonTimer.schedule(syncButtonTask, 3000);
        }
    }

    public void updatePlayersCollectionLogItems(ScriptPreFired preFired) {
        Object[] args = preFired.getScriptEvent().getArguments();
        if (args == null || args.length < 2) {
            return;
        }
        int itemId = (int) args[1];
        if (itemId > 0) {
            clogItemsUnlocked.add(itemId);
            // Only schedule sync if user has initiated a sync
            if (userInitiatedSync) {
                disableButton("Loading collection log items...");
                scheduleSync();
            }
        }
    }

    public void clearCollectionLog() {
        clogItemsUnlocked.clear();
        // Reset sync flag when clearing collection log
        userInitiatedSync = false;
    }

    public void enableButton() {
        if (interfaceManager.taskDashboard != null) {
            interfaceManager.taskDashboard.enableSyncButton();
        }
    }

    public void disableButton(String reason) {
        if (interfaceManager.taskDashboard != null) {
            interfaceManager.taskDashboard.disableSyncButton(reason);
        }
    }

    public void sync() {
        userInitiatedSync = true;
        disableButton("Loading collection log items...");
        refreshCollectionLog();
    }

    public void refreshCollectionLog() {
        clientThread.invokeLater(() -> {
            client.menuAction(-1, net.runelite.api.gameval.InterfaceID.Collection.SEARCH_TOGGLE, MenuAction.CC_OP, 1, -1, "Search", null);
            client.runScript(2240);
        });
    }

    public void syncClogWithProgress() {
        if (clogItemsUnlocked.isEmpty()) {
            return;
        }

        disableButton("Updating progress...");

        // Update completed tasks automatically
        for (TaskTier tier : TaskTier.values()) {
            for (Task task : taskService.getTaskList().getForTier(tier)) {
                if (!(task.getVerification() instanceof CollectionLogVerification)) {
                    continue;
                }

                CollectionLogVerification verif = (CollectionLogVerification) task.getVerification();
                int[] check = verif.getItemIds();
                int taskCount = verif.getCount();

                if (check.length == 0) {
                    continue;
                }

                int count = 0;
                for (int itemId : check) {
                    if (isCollectionLogItemUnlocked(itemId)) {
                        count++;
                    }
                }
                if (count >= taskCount && !plugin.isTaskCompleted(task.getId(), tier)) {
                    // Check passed, task not yet completed, mark as completed
                    plugin.completeTask(task.getId(), tier, false);
                    client.addChatMessage(net.runelite.api.ChatMessageType.GAMEMESSAGE, "", tier.displayName + " tier task '" + task.getName() + "' marked as <col=27ae60>completed.</col>", null);
                    log.debug("Task '{}' marked as completed for tier {}", task.getName(), tier.displayName);
                } else if (count < taskCount && plugin.isTaskCompleted(task.getId(), tier)) {
                    // Check failed, task marked as completed, unmark completion
                    plugin.completeTask(task.getId(), tier, false);
                    client.addChatMessage(net.runelite.api.ChatMessageType.GAMEMESSAGE, "", tier.displayName + " tier task '" + task.getName() + "' marked as <col=c0392b>incomplete.</col>", null);
                    log.debug("Task '{}' un-marked as this is not completed for tier {}", task.getName(), tier.displayName);
                }
            }
        }
        
        // Also sync achievement diary progress
        log.debug("Running achievement diary sync as part of collection log sync");
        achievementDiaryManager.sync();
        
        // Reset the flag after sync is complete
        userInitiatedSync = false;
        enableButton();
    }
}
