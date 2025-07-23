package com.logmaster.diary;

import com.logmaster.LogMasterPlugin;
import com.logmaster.domain.Task;
import com.logmaster.domain.TaskTier;
import com.logmaster.domain.verification.AchievementDiaryVerification;
import com.logmaster.task.TaskService;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.VarbitComposition;

import javax.inject.Inject;

@Slf4j
public class AchievementDiaryManager {

    @Inject
    private Client client;

    @Inject
    private TaskService taskService;

    @Inject
    private LogMasterPlugin plugin;

    public void initialise() {
    }

    public boolean isAchievementDiaryCompleted(String area, String tier) {
        AchievementDiaryMapping.DiaryInfo diaryInfo = AchievementDiaryMapping.getDiaryInfo(area, tier);
        if (diaryInfo == null) {
            log.warn("No diary mapping found for area '{}' and tier '{}'", area, tier);
            return false;
        }
        int varbit = diaryInfo.varbit;
        int value = diaryInfo.value;
        boolean isCompleted = getVarbitValue(varbit) == value;
        return isCompleted;
    }

    private int getVarbitValue(int varbitId)
    {
        VarbitComposition v = client.getVarbit(varbitId);
        if (v == null)
        {
            return -1;
        }

        int value = client.getVarpValue(v.getIndex());
        int lsb = v.getLeastSignificantBit();
        int msb = v.getMostSignificantBit();
        int mask = (1 << ((msb - lsb) + 1)) - 1;
        return (value >> lsb) & mask;
    }

    public void sync() {
        // Check all tasks across all tiers for achievement diary verification
        for (TaskTier tier : TaskTier.values()) {
            for (Task task : taskService.getTaskList().getForTier(tier)) {
                if (!(task.getVerification() instanceof AchievementDiaryVerification)) {
                    continue;
                }

                AchievementDiaryVerification verif = (AchievementDiaryVerification) task.getVerification();

                String achievementRegion = verif.getRegion();
                String achievementDifficulty = verif.getDifficulty();

                boolean isDiaryCompleted = isAchievementDiaryCompleted(achievementRegion, achievementDifficulty);
                boolean isTaskCompleted = plugin.isTaskCompleted(task.getId(), tier);

                if (isDiaryCompleted && !isTaskCompleted) {
                    // Diary is completed but task is not marked as completed - mark it as completed
                    plugin.completeTask(task.getId(), tier, false);
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                        tier.displayName + " tier task '" + task.getName() + "' marked as <col=27ae60>completed</col>.", null);
                    log.debug("Task '{}' marked as completed for tier {} (achievement diary completed)", task.getName(), tier.displayName);
                } else if (!isDiaryCompleted && isTaskCompleted) {
                    // Diary is not completed but task is marked as completed - unmark it
                    plugin.completeTask(task.getId(), tier, false);
                    client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", 
                        tier.displayName + " tier task '" + task.getName() + "' marked as <col=c0392b>incomplete</col>.", null);
                    log.debug("Task '{}' unmarked as completed for tier {} (achievement diary not completed)", task.getName(), tier.displayName);
                }
            }
        }
    }
}
