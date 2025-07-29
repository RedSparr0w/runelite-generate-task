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
import net.runelite.api.FontID;
import net.runelite.api.widgets.ItemQuantityMode;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.LinkBrowser;

import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
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

    private final static int BUTTON_HEIGHT = 30;
    private final static int BUTTON_WIDTH = 68;
    private final static int LARGE_BUTTON_WIDTH = 140;
    private final static int BACK_BUTTON_SPRITE_ID = -20037;
    private final static int BACK_BUTTON_HOVER_SPRITE_ID = -20038;
    private final static int WIKI_BUTTON_SPRITE_ID = -20039;
    private final static int WIKI_BUTTON_HOVER_SPRITE_ID = -20040;
    private final static int COMPLETE_TASK_SPRITE_ID = -20000;
    private final static int COMPLETE_TASK_HOVER_SPRITE_ID = -20002;
    private final static int INCOMPLETE_TASK_SPRITE_ID = -20041;
    private final static int INCOMPLETE_TASK_HOVER_SPRITE_ID = -20042;

    private TaskDashboard taskDashboard;
    private TaskList taskList;
    private TabManager tabManager;
    private UIPage previousVisiblePage = null;
    private Task currentTask = null;

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
        if (visible) {
            this.previousVisiblePage = this.taskDashboard.isVisible() ? this.taskDashboard : this.taskList;
            this.previousVisiblePage.setVisibility(false);
            this.tabManager.hideTabs();
        } else if (this.previousVisiblePage != null) {
            this.previousVisiblePage.setVisibility(true);
            if (this.taskList.isVisible()) {
                this.taskList.updateBounds();
            }
            this.tabManager.showTabs();
            this.tabManager.updateTabs();
            previousVisiblePage = null;
        }
    }

    private UILabel titleLabel;
    private UILabel tipLabel;
    private UILabel tierLabel;
    private UIButton wikiBtn;
    private UIButton closeBtn;
    private UIButton completeBtn;
    private UILabel progressLabel;
    private UIGraphic progressBarBg;
    private UIGraphic progressBarFill;
    private List<UIGraphic> taskIcons = new ArrayList<>();

    public void showTask(String taskId) {
        currentTask = taskService.getTaskById(taskId);

        // Show the task title
        if (titleLabel == null) {
            titleLabel = new UILabel(window.createChild(-1, WidgetType.TEXT));
        }
        titleLabel.setFont(FontID.BOLD_12);
        titleLabel.setText(currentTask.getName());
        titleLabel.getWidget().setHidden(false);
        titleLabel.getWidget().setTextColor(Color.WHITE.getRGB());
        titleLabel.getWidget().setTextShadowed(true);
        titleLabel.getWidget().setName(currentTask.getName());
        titleLabel.setSize(windowWidth, 20);
        this.add(titleLabel);

        // Show the task tip
        if (tipLabel == null) {
            tipLabel = new UILabel(window.createChild(-1, WidgetType.TEXT));
            this.add(tipLabel);
        }
        tipLabel.setFont(FontID.PLAIN_12);
        tipLabel.setText(currentTask.getTip());
        tipLabel.getWidget().setHidden(false);
        tipLabel.getWidget().setTextColor(Color.WHITE.getRGB());
        tipLabel.getWidget().setTextShadowed(true);
        tipLabel.getWidget().setName(currentTask.getName());
        Dimension descBounds = getTextDimension(tipLabel.getWidget(), tipLabel.getWidget().getText(), windowWidth - 40);
        tipLabel.setSize(windowWidth - 20, descBounds.height);

        int itemIndex = 0;
        if (currentTask.getVerification() instanceof CollectionLogVerification) {
            CollectionLogVerification verification = (CollectionLogVerification) currentTask.getVerification();
            int[] itemIds = verification.getItemIds();

            int obtainedCount = 0;
            int requiredCount = verification.getCount();
            for (int id : itemIds) {
                if (collectionLogService.isItemObtained(id)) {
                    obtainedCount++;
                }
            }

            int progressBarWidth = windowWidth - 20;
            int progressBarHeight = 18;

            // Create or reuse a UIGraphic for the progress bar background
            if (progressBarBg == null) {
                progressBarBg = new UIGraphic(window.createChild(-1, WidgetType.RECTANGLE));
                this.add(progressBarBg);
            }
            progressBarBg.setSize(progressBarWidth, progressBarHeight);
            progressBarBg.getWidget().setFilled(true);
            progressBarBg.getWidget().setOpacity(100);
            progressBarBg.getWidget().setTextColor(new Color(40, 40, 40).getRGB()); // dark background
            progressBarBg.getWidget().setBorderType(1);

            // Create or reuse a UIGraphic for the progress bar fill
            if (progressBarFill == null) {
                progressBarFill = new UIGraphic(window.createChild(-1, WidgetType.RECTANGLE));
                this.add(progressBarFill);
            }
            int fillWidth = Math.min((int) ((obtainedCount / (float) requiredCount) * progressBarWidth), progressBarWidth);
            progressBarFill.setSize(fillWidth, progressBarHeight);
            progressBarFill.getWidget().setFilled(true);
            progressBarFill.getWidget().setOpacity(100);
            progressBarFill.getWidget().setTextColor(new Color(60, 180, 75).getRGB()); // green fill
            progressBarFill.getWidget().setBorderType(0);

            // Progress label on top of the bar
            if (progressLabel == null) {
                progressLabel = new UILabel(window.createChild(-1, WidgetType.TEXT));
                this.add(progressLabel);
            }
            progressLabel.setFont(FontID.PLAIN_12);
            progressLabel.setText("Obtained " + obtainedCount + "/" + requiredCount + " required items");
            progressLabel.getWidget().setTextColor(Color.WHITE.getRGB());
            progressLabel.getWidget().setTextShadowed(true);
            progressLabel.getWidget().setName(currentTask.getName());
            progressLabel.setSize(progressBarWidth, progressBarHeight);

            if (itemIds != null && itemIds.length > 0) {
                int itemSize = 32;
                for (int itemId : itemIds) {
                    UIGraphic itemImage = itemIndex < taskIcons.size() ? taskIcons.get(itemIndex) : null;
                    if (itemImage == null) {
                        itemImage = new UIGraphic(window.createChild(-1, WidgetType.GRAPHIC));
                        taskIcons.add(itemImage);
                        this.add(itemImage);
                    }
                    itemImage.getWidget().setHidden(false);
                    itemImage.getWidget().setBorderType(1);
                    itemImage.getWidget().setItemQuantityMode(ItemQuantityMode.NEVER);
                    itemImage.setSize(itemSize, itemSize);
                    itemImage.setItem(itemId);
                    boolean isObtained = collectionLogService.isItemObtained(itemId);
                    itemImage.setOpacity(isObtained ? 1 : 0.3f);
                    String itemName = plugin.itemManager.getItemComposition(itemId).getName();
                    itemImage.getWidget().clearActions();
                    itemImage.clearActions();
                    itemImage.addAction(itemName, () -> {});
                    itemIndex++;
                }
            }
        } else {
            progressBarBg.setPosition(-100, -100);
            progressBarFill.setPosition(-100, -100);
            progressLabel.setPosition(-100, -100);
        }
        for (int i = itemIndex; i < taskIcons.size(); i++) {
            UIGraphic itemImage = taskIcons.get(i);
            if (itemImage != null) {
                itemImage.setPosition(-100, -100);
                itemImage.revalidate();
            }
        }

        if (wikiBtn == null) {
            wikiBtn = new UIButton(window.createChild(-1, WidgetType.GRAPHIC));
            this.add(wikiBtn);
        }
        wikiBtn.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        wikiBtn.setSprites(WIKI_BUTTON_SPRITE_ID, WIKI_BUTTON_HOVER_SPRITE_ID);
        wikiBtn.getWidget().clearActions();
        wikiBtn.clearActions();
        wikiBtn.addAction("View wiki", () -> {
            LinkBrowser.browse(currentTask.getWikiLink());
        });

        if (closeBtn == null) {
            closeBtn = new UIButton(window.createChild(-1, WidgetType.GRAPHIC));
            closeBtn.addAction("Close Task Info", this::closeTask);
            this.add(closeBtn);
        }
        closeBtn.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);
        closeBtn.setSprites(BACK_BUTTON_SPRITE_ID, BACK_BUTTON_HOVER_SPRITE_ID);

        if (completeBtn == null) {
            completeBtn = new UIButton(window.createChild(-1, WidgetType.GRAPHIC));
            this.add(completeBtn);
        }
        completeBtn.setSize(LARGE_BUTTON_WIDTH, BUTTON_HEIGHT);
        completeBtn.getWidget().clearActions();
        completeBtn.clearActions();
        if (taskService.isComplete(taskId)) {
            completeBtn.setSprites(INCOMPLETE_TASK_SPRITE_ID, INCOMPLETE_TASK_HOVER_SPRITE_ID);
            completeBtn.addAction("Mark as <col=c0392b>incomplete</col>", () -> toggleTask(taskId));
        } else {
            completeBtn.setSprites(COMPLETE_TASK_SPRITE_ID, COMPLETE_TASK_HOVER_SPRITE_ID);
            completeBtn.addAction("Mark as <col=27ae60>complete</col>", () -> toggleTask(taskId));
        }

        // Hide task list/dashboard/tabs, show task info
        this.setVisibility(true);
        this.setPositions();
    }

    private void toggleTask(String taskId) {
        completeBtn.getWidget().clearActions();
        completeBtn.clearActions();
        if (taskService.isComplete(taskId)) {
            taskService.uncomplete(taskId);
            completeBtn.setSprites(COMPLETE_TASK_SPRITE_ID, COMPLETE_TASK_HOVER_SPRITE_ID);
            completeBtn.addAction("Mark as <col=27ae60>complete</col>", () -> toggleTask(taskId));
        } else {
            taskService.complete(taskId);
            completeBtn.setSprites(INCOMPLETE_TASK_SPRITE_ID, INCOMPLETE_TASK_HOVER_SPRITE_ID);
            completeBtn.addAction("Mark as <col=c0392b>incomplete</col>", () -> toggleTask(taskId));
        }
        completeBtn.revalidate();
    }

    private void setPositions() {
        int offset_y = 0;
        titleLabel.setPosition(0, offset_y);
        offset_y += 30;
        tipLabel.setPosition(10, offset_y);
        Dimension descBounds = getTextDimension(tipLabel.getWidget(), tipLabel.getWidget().getText(), windowWidth - 40);
        offset_y += descBounds.height + 8;
        int progressBarX = 10;
        int progressBarY = offset_y;
        
        int itemIndex = 0;
        if (currentTask.getVerification() instanceof CollectionLogVerification) {
            CollectionLogVerification verification = (CollectionLogVerification) currentTask.getVerification();
            int[] itemIds = verification.getItemIds();
            progressBarBg.setPosition(progressBarX, progressBarY);
            progressBarFill.setPosition(progressBarX, progressBarY);
            progressLabel.setPosition(progressBarX, progressBarY);
            offset_y += 18;

            if (itemIds != null && itemIds.length > 0) {
                int itemSize = 32;
                int spacing = 8;
                int itemsPerRow = Math.max(1, (windowWidth - 20) / (itemSize + spacing));
                int numRows = (int) Math.ceil((double) itemIds.length / itemsPerRow);
                int startY = offset_y + 8;
                int y = startY;
                for (int row = 0; row < numRows; row++) {
                    int itemsInThisRow = Math.min(itemsPerRow, itemIds.length - itemIndex);
                    int totalRowWidth = itemsInThisRow * itemSize + (itemsInThisRow - 1) * spacing;
                    int startX = (windowWidth - totalRowWidth) / 2;
                    int x = startX;
                    // Check if this row would be too close to the bottom edge
                    if (y + itemSize > windowHeight - BUTTON_HEIGHT - 20) {
                        break;
                    }
                    for (int col = 0; col < itemsInThisRow; col++) {
                        UIGraphic itemImage = itemIndex < taskIcons.size() ? taskIcons.get(itemIndex) : null;
                        itemImage.setPosition(x, y);
                        x += itemSize + spacing;
                        itemImage.revalidate();
                        itemIndex++;
                    }
                    y += itemSize + spacing;
                }
                for (int i = itemIndex; i < taskIcons.size(); i++) {
                    UIGraphic itemImage = taskIcons.get(i);
                    if (itemImage != null) {
                        itemImage.setPosition(-100, -100);
                        itemImage.revalidate();
                    }
                }
                offset_y = y + 8;
            }
        } else {
            progressBarBg.setPosition(-100, -100);
            progressBarFill.setPosition(-100, -100);
            progressLabel.setPosition(-100, -100);
        }
        wikiBtn.setPosition(10, windowHeight - 10 - BUTTON_HEIGHT);
        completeBtn.setPosition((windowWidth / 2) - (LARGE_BUTTON_WIDTH / 2), windowHeight - 10 - BUTTON_HEIGHT);
        closeBtn.setPosition(windowWidth - 10 - BUTTON_WIDTH, windowHeight - 10 - BUTTON_HEIGHT);

        titleLabel.revalidate();
        tipLabel.revalidate();
        wikiBtn.revalidate();
        closeBtn.revalidate();
        completeBtn.revalidate();
        progressBarBg.revalidate();
        progressBarFill.revalidate();
        progressLabel.revalidate();
    }

    private Dimension getTextDimension(Widget widget, String text, int maxWidth) {
        if (text == null || text.isEmpty()) {
            return new Dimension(0, 0);
        }
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineCount = 1;
        int maxLineWidth = 0;
        for (int i = 0; i < words.length; i++) {
            String testLine = line.length() == 0 ? words[i] : line + " " + words[i];
            int testWidth = widget.getFont().getTextWidth(testLine);
            if (line.toString().contains("<br>") || testWidth > maxWidth && line.length() > 0) {
                // Start new line
                maxLineWidth = Math.max(maxLineWidth, widget.getFont().getTextWidth(line.toString()));
                line = new StringBuilder(words[i]);
                lineCount++;
            } else {
                if (line.length() > 0) {
                    line.append(" ");
                }
                line.append(words[i]);
            }
        }
        // Check last line
        if (line.length() > 0) {
            maxLineWidth = Math.max(maxLineWidth, widget.getFont().getTextWidth(line.toString()));
        }
        int lineHeight = widget.getFont().getBaseline();
        return new Dimension(maxLineWidth, lineCount * lineHeight);
    }

    public void closeTask() {
        // TODO: Implement closing the task info
        this.setVisibility(false);
    }

    public void updateBounds()
    {
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

        if (!this.isVisible()) {
            return;
        }

        this.setPositions();
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
