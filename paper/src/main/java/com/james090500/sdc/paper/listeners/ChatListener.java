package com.james090500.sdc.paper.listeners;

import com.james090500.sdc.common.api.events.DiscordMessageEvent;
import com.james090500.sdc.common.api.events.Subscribe;
import com.james090500.sdc.common.handlers.ChatHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class ChatListener {

    @EventHandler
    public void onChatEvent(AsyncChatEvent event) {
        Player sender = event.getPlayer();

        //Tempoary Chat format
        String format = String.format("%s: %s", sender.getName(), event.message());

        new ChatHandler(format);
    }

    @Subscribe
    public void onDiscordMessage(DiscordMessageEvent event) {
        Bukkit.getLogger().info("Discord Chat Message Sent");
        Component message = Component.text(String.format("%s: %s", event.getAuthorName(), event.getMessage()));
        Bukkit.getServer().sendMessage(message);
    }
}
