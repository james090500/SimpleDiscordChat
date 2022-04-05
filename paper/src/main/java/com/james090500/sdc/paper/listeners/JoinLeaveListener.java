package com.james090500.sdc.paper.listeners;

import com.james090500.sdc.common.handlers.JoinLeaveHandler;
import io.papermc.paper.text.PaperComponents;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveListener implements Listener {

    /**
     * The listener that runs on join
     * @param event The join event
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String username = PaperComponents.plainTextSerializer().serialize(player.displayName());
        new JoinLeaveHandler(player.getUniqueId(), username).join();
    }

    /**
     * The listener that runs on leave
     * @param event The leave event
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String username = PaperComponents.plainTextSerializer().serialize(player.displayName());
        new JoinLeaveHandler(player.getUniqueId(), username).leave();
    }
}
