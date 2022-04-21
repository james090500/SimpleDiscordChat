package com.james090500.sdc.paper.listeners;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.api.events.DiscordMessageEvent;
import com.james090500.sdc.common.api.events.Subscribe;
import com.james090500.sdc.common.handlers.ChatHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {

    /**
     * Runs when a user sends a chat message
     * @param event
     */
    @EventHandler
    public void onChatEvent(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        //Chat Format
        String chatFormat = SimpleDiscordChat.getInstance().getConfigs().getSettingsConfig().getFormat().getDiscord();

        //Fill placeholders
        String username = sender.getName();
        String displayName = PlainTextComponentSerializer.plainText().serialize(sender.displayName());
        String uuid = sender.getUniqueId().toString();
        String chatMessage = PlainTextComponentSerializer.plainText().serialize(event.message());

        //Set the variables
        chatFormat = chatFormat.replaceAll("%player_username%", username);
        chatFormat = chatFormat.replaceAll("%player_nick%", displayName);
        chatFormat = chatFormat.replaceAll("%player_uuid%", uuid);
        chatFormat = chatFormat.replaceAll("%message%", chatMessage);

        //Send to discord
        ChatHandler.sendMessage(chatFormat);
    }

    /**
     * Runs when a discord message is recieved
     * @param event The Discord message event
     */
    @Subscribe
    public void onDiscordMessage(DiscordMessageEvent event) {
        //Chat Format
        String chatFormat = SimpleDiscordChat.getInstance().getConfigs().getSettingsConfig().getFormat().getMinecraft();

        //Fill placeholders
        String username = event.getAuthorName();
        String chatMessage = event.getMessage();

        //Set the variables
        chatFormat = chatFormat.replaceAll("%username%", username);
        chatFormat = chatFormat.replaceAll("%message%", chatMessage);

        //The final chat message
        Component finalMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(chatFormat);
        Bukkit.getServer().sendMessage(finalMessage);
    }
}
