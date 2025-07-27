package com.logmaster.ui;

import com.logmaster.LogMasterConfig;
import com.logmaster.LogMasterPlugin;
import com.logmaster.domain.Task;
import com.logmaster.domain.TaskTier;
import com.logmaster.synchronization.SyncService;
import com.logmaster.synchronization.clog.CollectionLogService;
import com.logmaster.task.TaskService;
import com.logmaster.ui.component.TabManager;
import com.logmaster.ui.component.TaskDashboard;
import com.logmaster.ui.component.TaskList;
import com.logmaster.ui.generic.UICheckBox;
import com.logmaster.ui.generic.dropdown.UIDropdown;
import com.logmaster.ui.generic.dropdown.UIDropdownOption;
import com.logmaster.util.EventBusSubscriber;
import com.logmaster.util.FileUtils;
import net.runelite.api.Client;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.input.MouseWheelListener;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;

import static com.logmaster.LogMasterConfig.CONFIG_GROUP;
import static com.logmaster.ui.InterfaceConstants.DEF_FILE_SPRITES;

@Singleton
public class InterfaceManager extends EventBusSubscriber implements MouseListener, MouseWheelListener {
    private static final int COLLECTION_LOG_TAB_DROPDOWN_WIDGET_ID = 40697929;
    private static final int COLLECTION_LOG_SETUP_SCRIPT_ID = 7797;

    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private LogMasterConfig config;

    @Inject
    private LogMasterPlugin plugin;

	@Inject
	private MouseManager mouseManager;

    @Inject
    private SpriteManager spriteManager;

    @Inject
    private CollectionLogService collectionLogService;

    @Inject
    private SyncService syncService;

    @Inject
    private TaskService taskService;

    public TaskDashboard taskDashboard;
    private TaskList taskList;
    private TabManager tabManager;

    private UICheckBox taskDashboardCheckbox;
    private UIDropdown dropdown;

    public void startUp() {
        super.startUp();

        mouseManager.registerMouseListener(this);
        mouseManager.registerMouseWheelListener(this);

        SpriteDefinition[] spriteDefinitions = FileUtils.loadDefinitionResource(SpriteDefinition[].class, DEF_FILE_SPRITES);
        this.spriteManager.addSpriteOverrides(spriteDefinitions);
    }

    public void shutDown() {
        super.shutDown();

        mouseManager.unregisterMouseListener(this);
        mouseManager.unregisterMouseWheelListener(this);
    }

	@Subscribe
	public void onConfigChanged(ConfigChanged e) {
        if (!e.getGroup().equals(CONFIG_GROUP)) {
			return;
		}

        if (this.taskDashboard == null || !isTaskDashboardEnabled()) {
            return;
        }

        if (tabManager != null) {
            tabManager.updateTabs();

            List<TaskTier> visibleTiers = taskService.getVisibleTiers();
            TaskTier activeTier = plugin.getSelectedTier();
            if (activeTier != null && visibleTiers.contains(activeTier)) {
                tabManager.activateTaskDashboard();
            }
        }

        taskDashboard.updatePercentages();
        tabManager.onConfigChanged();
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded e) {
		if (e.getGroupId() != InterfaceID.COLLECTION) {
			return;
		}

        Widget window = client.getWidget(InterfaceID.Collection.CONTENT);

        createTaskDashboard(window);
        createTaskList(window);
        createTabManager(window);
        createTaskCheckbox();

        this.tabManager.updateTabs();
        this.taskDashboard.setVisibility(false);
	}

	@Subscribe
	public void onWidgetClosed(WidgetClosed e) {
		if (e.getGroupId() != InterfaceID.COLLECTION) {
			return;
		}

        this.taskDashboard.setVisibility(false);
        this.taskList.setVisibility(false);
        tabManager.hideTabs();
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired scriptPostFired) {
		if (scriptPostFired.getScriptId() != COLLECTION_LOG_SETUP_SCRIPT_ID) {
			return;
		}

        if (this.dropdown != null) {
            this.dropdown.cleanup();
            this.dropdown = null;
        }

        createTaskDropdownOption();
	}

	@Subscribe
	public void onGameTick(GameTick e) {
        if (this.taskList != null) {
            taskList.updateBounds();
        }
        if (this.taskDashboard != null) {
            taskDashboard.updateBounds();
        }
        if (this.tabManager != null) {
            tabManager.updateBounds();
        }
        if (this.taskDashboardCheckbox != null) {
            Widget window = client.getWidget(621, 88);
            if (window != null) {
                taskDashboardCheckbox.alignToRightEdge(window, 35, 10);
            }
        }
	}

    public boolean isDashboardOpen() {
        return this.taskDashboard != null && this.taskDashboard.isVisible();
    }

    public void handleMouseWheel(MouseWheelEvent event) {
        if (this.taskList != null) {
            taskList.handleWheel(event);
        }
    }

    public void handleMousePress(int mouseX, int mouseY) {
        if (this.taskList != null && this.taskList.isVisible()) {
            taskList.handleMousePress(mouseX, mouseY);
        }
    }

    public void handleMouseDrag(int mouseX, int mouseY) {
        if (this.taskList != null && this.taskList.isVisible()) {
            taskList.handleMouseDrag(mouseX, mouseY);
        }
    }

    public void handleMouseRelease() {
        if (this.taskList != null) {
            taskList.handleMouseRelease();
        }
    }

    @Override
    public MouseWheelEvent mouseWheelMoved(MouseWheelEvent event) {
        handleMouseWheel(event);
        return event;
    }

    @Override
    public MouseEvent mouseClicked(MouseEvent event) {
        return event;
    }

    @Override
    public MouseEvent mousePressed(MouseEvent event) {
        handleMousePress(event.getX(), event.getY());
        return event;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent event) {
        handleMouseRelease();
        return event;
    }

    @Override
    public MouseEvent mouseDragged(MouseEvent event) {
        handleMouseDrag(event.getX(), event.getY());
        return event;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent event) {
        return event;
    }

    @Override
    public MouseEvent mouseEntered(MouseEvent event) {
        return event;
    }

    @Override
    public MouseEvent mouseExited(MouseEvent event) {
        return event;
    }

    private void createTaskDropdownOption() {
        Widget container = client.getWidget(COLLECTION_LOG_TAB_DROPDOWN_WIDGET_ID);
        if (container == null) {
            return;
        }

        this.dropdown = new UIDropdown(container);
        this.dropdown.addOption("Tasks", "View Tasks Dashboard");
        this.dropdown.setOptionEnabledListener(this::toggleTaskDashboard);
    }

    private void createTabManager(Widget window) {
        this.tabManager = new TabManager(window, config, plugin);
        this.tabManager.setComponents(taskDashboard, taskList);
    }

    private void createTaskDashboard(Widget window) {
        this.taskDashboard = new TaskDashboard(plugin, config, window, syncService, taskService, client);
        this.taskDashboard.setVisibility(false);
    }

    private void createTaskList(Widget window) {
        this.taskList = new TaskList(window, plugin, clientThread, config, collectionLogService, taskService);
        this.taskList.setVisibility(false);
    }

    private void createTaskCheckbox() {
        Widget window = client.getWidget(621, 88);
        if (window != null) {
            // Create the graphic widget for the checkbox
            Widget toggleWidget = window.createChild(-1, WidgetType.GRAPHIC);
            Widget labelWidget = window.createChild(-1, WidgetType.TEXT);

            // Wrap in checkbox, set size, position, etc.
            taskDashboardCheckbox = new UICheckBox(toggleWidget, labelWidget);
            taskDashboardCheckbox.setPosition(360, 10);
            taskDashboardCheckbox.setName("Task Dashboard");
            taskDashboardCheckbox.setEnabled(false);
            taskDashboardCheckbox.setText("Task Dashboard");
            labelWidget.setPos(375, 10);
                    

            taskDashboardCheckbox.setToggleListener((UICheckBox src) -> {
                if (taskDashboardCheckbox.isEnabled()) {
                    this.dropdown.setEnabledOption("Tasks");
                } else {
                    this.dropdown.setEnabledOption("View Log");
                }
            });
        }
    }

    private void toggleTaskDashboard(UIDropdownOption src) {
        if(this.taskDashboard == null) return;

        Task activeTask = taskService.getActiveTask();
        if (activeTask != null) {
            this.taskDashboard.setTask(activeTask.getName(), activeTask.getDisplayItemId(), null);
            this.taskDashboard.disableGenerateTask();
        } else {
            clearCurrentTask();
        }

        boolean enabled = isTaskDashboardEnabled();
        
        
        this.taskDashboardCheckbox.setEnabled(enabled);
        Widget contentWidget = client.getWidget(InterfaceID.Collection.CONTENT);
        if (contentWidget != null) {
            for (Widget c : contentWidget.getStaticChildren()) {
                c.setHidden(enabled);
            }
        }
        Widget searchTitleWidget = client.getWidget(InterfaceID.Collection.SEARCH_TITLE);
        if (searchTitleWidget != null) {
            searchTitleWidget.setHidden(enabled);
        }

        if (enabled) {
            this.tabManager.activateTaskDashboard();
        } else {
            this.taskDashboard.setVisibility(false);
            this.taskList.setVisibility(false);
            this.tabManager.hideTabs();
        }

        // *Boop*
        this.client.playSoundEffect(SoundEffectID.UI_BOOP);
    }

    private boolean isTaskDashboardEnabled() {
        return this.dropdown != null && this.dropdown.getEnabledOption().getText().equals("Tasks");
    }

    public void completeTask() {
        boolean wasDashboardVisible = this.taskDashboard.isVisible();
        this.taskDashboard.updatePercentages();
        taskList.refreshTasks(0);
        // Restore previous visibility state
        this.taskDashboard.setVisibility(wasDashboardVisible);
        this.taskList.setVisibility(!wasDashboardVisible);
        this.tabManager.showTabs();
    }

    public void clearCurrentTask() {
        this.taskDashboard.setTask("No task.", -1, null);
        this.taskDashboard.enableGenerateTask();
        this.taskDashboard.enableFaqButton();
    }
}
