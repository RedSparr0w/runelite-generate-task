package com.logmaster;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.logmaster.LogMasterConfig.CONFIG_GROUP;
import static com.logmaster.LogMasterConfig.PLUGIN_VERSION_KEY;

@Slf4j
@Singleton
public class PluginUpdateNotifier {
    private static final String PLUGIN_VERSION = "1.1.0";

    private static final String UPDATE_MESSAGE =
            "<colHIGHLIGHT>Collection Log Master v" + PLUGIN_VERSION + "<br>"
            + "<colHIGHLIGHT>- Added task synchronization";

    @Inject
    EventBus eventBus;

    @Inject
    ConfigManager configManager;

    @Inject
    ChatMessageManager chatMessageManager;

    public void startUp() {
        eventBus.register(this);
    }

    public void shutDown() {
        eventBus.unregister(this);
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged) {
        GameState gameState = gameStateChanged.getGameState();
        if (gameState == GameState.LOGGED_IN) {
            checkUpdate();
        }
    }

    private void checkUpdate() {
        String lastVersion = configManager.getRSProfileConfiguration(CONFIG_GROUP, PLUGIN_VERSION_KEY);

        if (!PLUGIN_VERSION.equals(lastVersion)) {
            configManager.setRSProfileConfiguration(CONFIG_GROUP, PLUGIN_VERSION_KEY, PLUGIN_VERSION);
            notifyUpdate();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void notifyUpdate() {
        if (UPDATE_MESSAGE == null) return;

        chatMessageManager.queue(
                QueuedMessage.builder()
                        .type(ChatMessageType.CONSOLE)
                        .runeLiteFormattedMessage(UPDATE_MESSAGE)
                        .build()
        );
    }
}
