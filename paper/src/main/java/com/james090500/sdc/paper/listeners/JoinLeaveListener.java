package com.james090500.sdc.paper.listeners;

import com.james090500.sdc.common.handlers.JoinLeaveHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
        new JoinLeaveHandler(player.getUniqueId(), getUsername(player.displayName())).join();
    }

    /**
     * The listener that runs on leave
     * @param event The leave event
     */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        new JoinLeaveHandler(player.getUniqueId(), getUsername(player.displayName())).leave();
    }

    /**
     * Converts a componenet to a string
     * @param component The displayName componenet
     * @return The display name as a string
     */
    private String getUsername(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }
}
