package com.james090500.sdc.velocity.listeners;

import com.james090500.sdc.common.handlers.JoinLeaveHandler;
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
        JoinLeaveHandler.join(event.getPlayer().getUniqueId());
    }

    /**
     * The listener that runs on leave
     * @param event The leave event
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        JoinLeaveHandler.leave(event.getPlayer().getUniqueId());
    }
}
