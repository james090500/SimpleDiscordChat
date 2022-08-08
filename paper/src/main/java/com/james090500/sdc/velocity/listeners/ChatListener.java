package com.james090500.sdc.velocity.listeners;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.handlers.ChatHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    /**
     * Runs when a user sends a chat message
     * @param event
     */
    @EventHandler
    public void onChatEvent(AsyncChatEvent event) {
        //Chat Format
        String chatFormat = Configs.getSettingsConfig().getFormat().getDiscord();

        //Fill placeholders
        String chatMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        chatFormat = chatFormat.replaceAll("%message%", chatMessage);
        chatFormat = SimpleDiscordChat.getInstance().getServerInterface().parsePlaceholders(event.getPlayer().getUniqueId(), chatFormat, true);

        //Send to discord
        ChatHandler.sendMessage(chatFormat);
    }
}
