package com.james090500.sdc.velocity.listeners;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.api.SDCPlayer;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.handlers.ChatHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;

public class ChatListener {

    /**
     * Runs when a user sends a chat message
     * @param event
     */
    @Subscribe
    public void onChatEvent(PlayerChatEvent event) {
        //Chat Format
        String chatFormat = Configs.getSettingsConfig().getFormat().getDiscord();

        //Fill placeholders
        chatFormat = chatFormat.replaceAll("%message%", event.getMessage());
        chatFormat = SimpleDiscordChat.getInstance().getServerInterface().parsePlaceholders(SDCPlayer.get(event.getPlayer().getUniqueId()), chatFormat, true);

        //Send to discord
        ChatHandler.sendMessage(chatFormat);
    }
}
