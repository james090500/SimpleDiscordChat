package com.james090500.sdc.paper.listeners;

import com.james090500.sdc.common.handlers.JoinLeaveHandler;
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
        JoinLeaveHandler.join(player.getUniqueId());
    }

    /**
     * The listener that runs on leave
     * @param event The leave event
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        JoinLeaveHandler.leave(event.getPlayer().getUniqueId());
    }

    /**
     * Call a leave
     * @param player The player leaving
     */
    public static void onLeave(Player player) {
        JoinLeaveHandler.leave(player.getUniqueId());
    }
}
