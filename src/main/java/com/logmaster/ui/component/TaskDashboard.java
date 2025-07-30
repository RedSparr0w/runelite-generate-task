package com.logmaster.ui.component;

import com.logmaster.LogMasterConfig;
import com.logmaster.LogMasterPlugin;
import com.logmaster.domain.Task;
import com.logmaster.domain.TaskTier;
import com.logmaster.synchronization.SyncService;
import com.logmaster.task.TaskService;
import com.logmaster.ui.generic.UIButton;
import com.logmaster.ui.generic.UIGraphic;
import com.logmaster.ui.generic.UILabel;
import com.logmaster.ui.generic.UIPage;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.FontID;
import net.runelite.api.SoundEffectID;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.logmaster.ui.InterfaceConstants.COLLECTION_LOG_WINDOW_HEIGHT;
import static com.logmaster.ui.InterfaceConstants.COLLECTION_LOG_WINDOW_WIDTH;

public class TaskDashboard extends UIPage {
    private final static int DEFAULT_BUTTON_WIDTH = 140;
    private final static int DEFAULT_BUTTON_HEIGHT = 30;
    private final static int SMALL_BUTTON_WIDTH = 68;
    private final static int DEFAULT_TASK_DETAILS_WIDTH = 300;
    private final static int DEFAULT_TASK_DETAILS_HEIGHT = 75;
    private final static int GENERATE_TASK_SPRITE_ID = -20001;
    private final static int COMPLETE_TASK_SPRITE_ID = -20000;
    private final static int GENERATE_TASK_HOVER_SPRITE_ID = -20003;
    private final static int COMPLETE_TASK_HOVER_SPRITE_ID = -20002;
    private final static int GENERATE_TASK_DISABLED_SPRITE_ID = -20005;
    private final static int COMPLETE_TASK_DISABLED_SPRITE_ID = -20004;
    private final static int TASK_BACKGROUND_SPRITE_ID = -20006;
    private final static int FAQ_BUTTON_SPRITE_ID = -20027;
    private final static int FAQ_BUTTON_HOVER_SPRITE_ID = -20028;
    private final static int SYNC_BUTTON_SPRITE_ID = -20034;
    private final static int SYNC_BUTTON_HOVER_SPRITE_ID = -20035;
    private final static int SYNC_BUTTON_DISABLED_SPRITE_ID = -20036;

    @Getter
    private Widget window;
    private LogMasterPlugin plugin;

    private LogMasterConfig config;
    private final SyncService syncService;
    private final TaskService taskService;
    private final Client client;
    private final TaskInfo taskInfo;


    private UILabel title;
    private UILabel taskLabel;
    private UILabel percentCompletion;

    private UIGraphic taskImage;
    private UIGraphic taskBg;

    private UIButton completeTaskBtn;
    private UIButton generateTaskBtn;
    private UIButton faqBtn;
    private UIButton syncBtn;

    public TaskDashboard(LogMasterPlugin plugin, LogMasterConfig config, Widget window, SyncService syncService, TaskService taskService, Client client, TaskInfo taskInfo) {
        this.window = window;
        this.plugin = plugin;
        this.config = config;
        this.syncService = syncService;
        this.taskService = taskService;
        this.client = client;
        this.taskInfo = taskInfo;

        createTaskDetails();

        Widget titleWidget = window.createChild(-1, WidgetType.TEXT);
        this.title = new UILabel(titleWidget);
        this.title.setFont(FontID.QUILL_CAPS_LARGE);
        this.title.setSize(COLLECTION_LOG_WINDOW_WIDTH, DEFAULT_TASK_DETAILS_HEIGHT);
        this.title.setPosition(getCenterX(window, COLLECTION_LOG_WINDOW_WIDTH), 24);
        this.title.setText("Current Task");

        Widget percentWidget = window.createChild(-1, WidgetType.TEXT);
        this.percentCompletion = new UILabel(percentWidget);
        this.percentCompletion.setFont(FontID.BOLD_12);
        this.percentCompletion.setSize(COLLECTION_LOG_WINDOW_WIDTH, 25);
        this.percentCompletion.setPosition(getCenterX(window, COLLECTION_LOG_WINDOW_WIDTH), COLLECTION_LOG_WINDOW_HEIGHT - 91);
        updatePercentages();

        Widget completeTaskWidget = window.createChild(-1, WidgetType.GRAPHIC);
        this.completeTaskBtn = new UIButton(completeTaskWidget);
        this.completeTaskBtn.setSize(DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
        this.completeTaskBtn.setPosition(getCenterX(window, DEFAULT_BUTTON_WIDTH) + (DEFAULT_BUTTON_WIDTH / 2 + 15), getCenterY(window, DEFAULT_BUTTON_HEIGHT) + 62);
        this.completeTaskBtn.setSprites(COMPLETE_TASK_SPRITE_ID, COMPLETE_TASK_HOVER_SPRITE_ID);

        Widget generateTaskWidget = window.createChild(-1, WidgetType.GRAPHIC);
        this.generateTaskBtn = new UIButton(generateTaskWidget);
        this.generateTaskBtn.setSize(DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
        this.generateTaskBtn.setPosition(getCenterX(window, DEFAULT_BUTTON_WIDTH) - (DEFAULT_BUTTON_WIDTH / 2 + 15), getCenterY(window, DEFAULT_BUTTON_HEIGHT) + 62);
        this.generateTaskBtn.setSprites(GENERATE_TASK_SPRITE_ID, GENERATE_TASK_HOVER_SPRITE_ID);

        Widget faqWidget = window.createChild(-1, WidgetType.GRAPHIC);
        this.faqBtn = new UIButton(faqWidget);
        this.faqBtn.setSize(SMALL_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
        this.faqBtn.setPosition(getCenterX(window, SMALL_BUTTON_WIDTH) + 190, getCenterY(window, DEFAULT_BUTTON_HEIGHT) + 112);
        this.faqBtn.setSprites(FAQ_BUTTON_SPRITE_ID, FAQ_BUTTON_HOVER_SPRITE_ID);

        
        Widget syncWidget = window.createChild(-1, WidgetType.GRAPHIC);
        this.syncBtn = new UIButton(syncWidget);
        this.syncBtn.setSize(SMALL_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT);
        this.syncBtn.setPosition(getCenterX(window, SMALL_BUTTON_WIDTH) - 190, getCenterY(window, DEFAULT_BUTTON_HEIGHT) + 112);
        this.syncBtn.setSprites(SYNC_BUTTON_SPRITE_ID, SYNC_BUTTON_DISABLED_SPRITE_ID);

        this.add(this.title);
        this.add(this.taskBg);
        this.add(this.taskLabel);
        this.add(this.taskImage);
        this.add(this.completeTaskBtn);
        this.add(this.generateTaskBtn);
        this.add(this.percentCompletion);
        this.add(faqBtn);
        this.add(syncBtn);
    }

    @Override
    public void setVisibility(boolean visible) {
        super.setVisibility(visible);
        if (visible) {
            if (taskService.getActiveTask() == null) {
                clearTask();
            } else {
                Task activeTask = taskService.getActiveTask();
                setTask(activeTask, null);
            }
        }
    }

    private void createTaskDetails() {
        final int POS_X = getCenterX(window, DEFAULT_TASK_DETAILS_WIDTH);
        final int POS_Y = getCenterY(window, DEFAULT_TASK_DETAILS_HEIGHT)-3;

        Widget taskBgWidget = window.createChild(-1, WidgetType.GRAPHIC);
        this.taskBg = new UIGraphic(taskBgWidget);
        this.taskBg.setSize(DEFAULT_TASK_DETAILS_WIDTH, DEFAULT_TASK_DETAILS_HEIGHT);
        this.taskBg.setPosition(POS_X, POS_Y);
        this.taskBg.setSprite(TASK_BACKGROUND_SPRITE_ID);

        Widget label = window.createChild(-1, WidgetType.TEXT);
        label.setTextColor(Color.WHITE.getRGB());
        label.setTextShadowed(true);
        label.setName("Task Label");
        this.taskLabel = new UILabel(label);
        this.taskLabel.setFont(496);
        this.taskLabel.setPosition(POS_X+60, POS_Y);
        this.taskLabel.setSize(DEFAULT_TASK_DETAILS_WIDTH-60, DEFAULT_TASK_DETAILS_HEIGHT);

        Widget taskImageWidget = window.createChild(-1, WidgetType.GRAPHIC);
        this.taskImage = new UIGraphic(taskImageWidget);
        this.taskImage.setPosition(POS_X+12, POS_Y+21);
        this.taskImage.setSize(42, 36);
        this.taskImage.getWidget().setBorderType(1);
    }

    public void clearTask() {
        this.taskBg.getWidget().clearActions();
        this.taskBg.clearActions();
        this.taskLabel.setText("No active task.");
        this.taskImage.setItem(7542);
        this.disableCompleteTask();
        this.enableGenerateTask();
        this.enableFaqButton();
    }

    public void setTask(Task task, List<Task> cyclingTasks) {
        this.disableGenerateTask();

        if (cyclingTasks != null) {
            for (int i = 0; i < 250; i++) {
                Task displayTask = cyclingTasks.get((int) Math.floor(Math.random() * cyclingTasks.size()));
                // Seems the most natural timing
                double decay = 450.0 / ((double) config.rollTime());
                int delay = (int) ((config.rollTime() * 0.925) * Math.exp(-decay * i));
                Timer fakeTaskTimer = new Timer(delay, ae -> {
                    this.taskLabel.setText(displayTask.getName());
                    this.taskImage.setItem(displayTask.getDisplayItemId());
                });
                fakeTaskTimer.setRepeats(false);
                fakeTaskTimer.setCoalesce(true);
                fakeTaskTimer.start();
            }
            Timer realTaskTimer = new Timer(config.rollTime(), ae -> {
                setTask(task, null);
            });
            realTaskTimer.setRepeats(false);
            realTaskTimer.setCoalesce(true);
            realTaskTimer.start();
            return;
        }

        this.taskLabel.setText(task.getName());
        this.taskImage.setItem(task.getDisplayItemId());
        this.taskBg.addAction("View task info", () -> taskInfo.showTask(task.getId()));
        this.enableCompleteTask();
        this.enableFaqButton();
    }

	private void generateTask() {
		client.playSoundEffect(SoundEffectID.UI_BOOP);
		Task generatedTask = taskService.generate();

		List<Task> rollTaskList = config.rollPastCompleted() ? taskService.getTierTasks() : taskService.getIncompleteTierTasks();
		setTask(generatedTask, rollTaskList);
        disableGenerateTask();
        updatePercentages();
	}

    public void updatePercentages() {
        Map<TaskTier, Float> progress = taskService.getProgress();
        TaskTier currentTier = taskService.getCurrentTier();
        float tierPercentage = progress.get(currentTier);

        String text = String.format(
                "<col=%s>%d%%</col> %s Completed",
                getCompletionColor(tierPercentage),
                (int) tierPercentage,
                currentTier.displayName
        );
        percentCompletion.setText(text);
    }

    private String getCompletionColor(double percent) {
        int max = 255;
        int amount = (int) Math.round(((percent % 50) / 50) * max);

        if(percent == 100) {
            return "00ff00";
        }
        else if(percent > 50) {
            int redValue = max - amount;
            return String.format("%02x", redValue)+"ff00";

        }
        else if(percent == 50) {
            return "ffff00";
        }
        else {
            return "ff"+String.format("%02x", amount)+"00";
        }
    }


    public void disableGenerateTask() {
        this.generateTaskBtn.setSprites(GENERATE_TASK_DISABLED_SPRITE_ID);
        this.generateTaskBtn.clearActions();

        this.generateTaskBtn.addAction("Disabled", this::playFailSound);
    }

    public void enableGenerateTask() {
        this.generateTaskBtn.clearActions();
        this.generateTaskBtn.setSprites(GENERATE_TASK_SPRITE_ID, GENERATE_TASK_HOVER_SPRITE_ID);
        this.generateTaskBtn.addAction("Generate task", this::generateTask);

        this.disableCompleteTask();
    }

    public void disableCompleteTask() {
        this.completeTaskBtn.setSprites(COMPLETE_TASK_DISABLED_SPRITE_ID);
        this.completeTaskBtn.clearActions();
        this.completeTaskBtn.addAction("Disabled", this::playFailSound);
    }

    public void enableCompleteTask() {
        this.completeTaskBtn.clearActions();
        this.completeTaskBtn.setSprites(COMPLETE_TASK_SPRITE_ID, COMPLETE_TASK_HOVER_SPRITE_ID);
        this.completeTaskBtn.addAction("Complete", plugin::completeTask);
    }

    public void enableFaqButton() {
        this.faqBtn.clearActions();
        this.faqBtn.setSprites(FAQ_BUTTON_SPRITE_ID, FAQ_BUTTON_HOVER_SPRITE_ID);
        this.faqBtn.addAction("FAQ", plugin::visitFaq);
        this.enableSyncButton();
    }

    public void enableSyncButton() {
        this.syncBtn.clearActions();
        this.syncBtn.setSprites(SYNC_BUTTON_SPRITE_ID, SYNC_BUTTON_HOVER_SPRITE_ID);
        this.syncBtn.addAction("Auto sync completed tasks", syncService::sync);
    }

    public void updateBounds() {
        int windowWidth = window.getWidth();

        // Update title position - force widget position update
        int titleX = getCenterX(window, COLLECTION_LOG_WINDOW_WIDTH);
        this.title.setPosition(titleX, 24);
        this.title.getWidget().setPos(titleX, 24);

        // Update task details (background, label, image)
        final int taskPosX = getCenterX(window, DEFAULT_TASK_DETAILS_WIDTH);
        final int taskPosY = getCenterY(window, DEFAULT_TASK_DETAILS_HEIGHT) - 3;
        
        this.taskBg.setPosition(taskPosX, taskPosY);
        this.taskBg.getWidget().setPos(taskPosX, taskPosY);
        
        this.taskLabel.setPosition(taskPosX + 60, taskPosY);
        this.taskLabel.getWidget().setPos(taskPosX + 60, taskPosY);
        
        this.taskImage.setPosition(taskPosX + 12, taskPosY + 21);
        this.taskImage.getWidget().setPos(taskPosX + 12, taskPosY + 21);

        // Update button positions - force widget position updates
        int generateBtnX = getCenterX(window, DEFAULT_BUTTON_WIDTH) - (DEFAULT_BUTTON_WIDTH / 2 + 15);
        int generateBtnY = getCenterY(window, DEFAULT_BUTTON_HEIGHT) + 62;
        this.generateTaskBtn.setPosition(generateBtnX, generateBtnY);
        this.generateTaskBtn.getWidget().setPos(generateBtnX, generateBtnY);
        
        int completeBtnX = getCenterX(window, DEFAULT_BUTTON_WIDTH) + (DEFAULT_BUTTON_WIDTH / 2 + 15);
        int completeBtnY = getCenterY(window, DEFAULT_BUTTON_HEIGHT) + 62;
        this.completeTaskBtn.setPosition(completeBtnX, completeBtnY);
        this.completeTaskBtn.getWidget().setPos(completeBtnX, completeBtnY);
        
        // Update FAQ button position with boundary checking
        int faqBtnX = getCenterX(window, SMALL_BUTTON_WIDTH) + 238;
        int faqBtnY = getCenterY(window, DEFAULT_BUTTON_HEIGHT) + 112;
        
        // Check if FAQ button would go outside the window and align with edge if needed
        int faqBtnWidth = SMALL_BUTTON_WIDTH;
        if (faqBtnX + faqBtnWidth + 10 > windowWidth) {
            faqBtnX = windowWidth - faqBtnWidth - 10; // 10px margin from edge
        }
        this.faqBtn.setPosition(faqBtnX, faqBtnY);
        this.faqBtn.getWidget().setPos(faqBtnX, faqBtnY);

        // Update Sync button position with boundary checking
        int syncBtnX = getCenterX(window, SMALL_BUTTON_WIDTH) - 238;
        int syncBtnY = getCenterY(window, DEFAULT_BUTTON_HEIGHT) + 112;
        if (syncBtnX < 10) {
            syncBtnX = 10; // 10px margin from left edge
        }
        this.syncBtn.setPosition(syncBtnX, syncBtnY);
        this.syncBtn.getWidget().setPos(syncBtnX, syncBtnY);
        
        // Update percentage completion position - force widget position update
        int percentX = getCenterX(window, COLLECTION_LOG_WINDOW_WIDTH);
        int percentY = getCenterY(window, DEFAULT_BUTTON_HEIGHT) + 112; // Same Y as FAQ button
        this.percentCompletion.setPosition(percentX, percentY);
        this.percentCompletion.getWidget().setPos(percentX, percentY);
        
        // Force revalidation of all widgets
        this.title.getWidget().revalidate();
        this.taskBg.getWidget().revalidate();
        this.taskLabel.getWidget().revalidate();
        this.taskImage.getWidget().revalidate();
        this.generateTaskBtn.getWidget().revalidate();
        this.completeTaskBtn.getWidget().revalidate();
        this.faqBtn.getWidget().revalidate();
        this.syncBtn.getWidget().revalidate();
        this.percentCompletion.getWidget().revalidate();
    }

	private int getCenterX(Widget window, int width) {
		return (window.getWidth() / 2) - (width / 2);
	}

	private int getCenterY(Widget window, int height) {
		return (window.getHeight() / 2) - (height / 2);
	}

	private void playFailSound() {
		client.playSoundEffect(2277);
	}
}
