package com.logmaster.ui.component;

import com.logmaster.LogMasterConfig;
import com.logmaster.LogMasterPlugin;
import com.logmaster.domain.DynamicTaskImages;
import com.logmaster.domain.Task;
import com.logmaster.domain.TaskTier;
import com.logmaster.domain.verification.clog.CollectionLogVerification;
import com.logmaster.synchronization.clog.CollectionLogService;
import com.logmaster.task.TaskService;
import com.logmaster.ui.generic.UIButton;
import com.logmaster.ui.generic.UIGraphic;
import com.logmaster.ui.generic.UILabel;
import com.logmaster.ui.generic.UIPage;
import com.logmaster.ui.component.TaskList;
import com.logmaster.ui.component.TabManager;
import com.logmaster.ui.component.TaskDashboard;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import java.util.List;

import static com.logmaster.ui.InterfaceConstants.*;

@Slf4j
public class TaskInfo extends UIPage {
    private final static int OFFSET_X = 0;
    private final static int OFFSET_Y = 21;
    

    private final Widget window;
    private final LogMasterPlugin plugin;
    private final ClientThread clientThread;
    private final CollectionLogService collectionLogService;
    private final TaskService taskService;

    private TaskDashboard taskDashboard;
    private TaskList taskList;
    private TabManager tabManager;

    private Rectangle bounds = new Rectangle();
    private int windowWidth = 480;
    private int windowHeight = 252;
    private int wrapperX = 0;
    private int wrapperY = 0;
    private int wrapperHeight = 231;
    private int windowX = 0;
    private int windowY = 0;

    private final LogMasterConfig config;

    public TaskInfo(Widget window, LogMasterPlugin plugin, ClientThread clientThread, LogMasterConfig config, CollectionLogService collectionLogService, TaskService taskService) {
        this.window = window;
        this.plugin = plugin;
        this.clientThread = clientThread;
        this.config = config;
        this.collectionLogService = collectionLogService;
        this.taskService = taskService;

        updateBounds();
    }

    public void setComponents(TaskDashboard taskDashboard, TaskList taskList, TabManager tabManager) {
        this.taskDashboard = taskDashboard;
        this.taskList = taskList;
        this.tabManager = tabManager;
    }

    @Override
    public void setVisibility(boolean visible) {
        super.setVisibility(visible);
        this.taskDashboard.setVisibility(!visible);
        this.taskList.setVisibility(!visible);
        // this.tabManager.setVisibility(!visible);
    }

    public void showTask(String taskId) {
        TaskTier relevantTier = plugin.getSelectedTier();
        if (relevantTier == null) {
            relevantTier = TaskTier.MASTER;
        }
        // TODO: Implement logic to find the task by ID
        Task task = taskService.getTaskById(taskId, relevantTier);

        // Hide task list/dashboard/tabs, show task info
        this.setVisibility(true);

        // TODO: Show the task information for the given task index
        // Show the task title/description
        // Show the task tier?
        // Show the task icon?
        // Show the task tip
        // Show the task progress and status
        // Show the task items (if applicable), and status of each item
        // Add a button to close the task info
        // Add a button to mark the task as complete/incomplete
    }

    public void closeTask() {
        // TODO: Implement closing the task info
        this.setVisibility(false);
    }

    public void updateBounds()
    {
        if (!this.isVisible()) {
            return;
        }

        Widget wrapper = window.getParent();
        wrapperX = wrapper.getRelativeX();
        wrapperY = wrapper.getRelativeY();
        wrapperHeight = window.getHeight() - OFFSET_Y;
        windowX = window.getRelativeX();
        windowY = window.getRelativeY();
        windowWidth = window.getWidth();
        windowHeight = window.getHeight();

        bounds.setLocation(wrapperX + windowX + OFFSET_X, wrapperY + windowY + OFFSET_Y);
        bounds.setSize(windowWidth - OFFSET_X, wrapperHeight);
    }

    private void forceWidgetPositionUpdate(Widget button, int x, int y) {
        button.setPos(x, y);
        button.revalidate();
    }

    private void forceWidgetUpdate(Widget widget, int width, int height) {
        widget.setSize(width, height);
        widget.revalidate();
    }
}
