package com.james090500.sdc.paper.listeners;

import com.james090500.sdc.common.handlers.ChatHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
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

}
