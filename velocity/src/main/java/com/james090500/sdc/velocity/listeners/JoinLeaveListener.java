package com.james090500.sdc.velocity.listeners;

import com.james090500.sdc.common.api.SDCPlayer;
import com.james090500.sdc.common.handlers.JoinLeaveHandler;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;

public class JoinLeaveListener {

    /**
     * The listener that runs on join
     * @param event The join event
     */
    @Subscribe
    public void onJoin(PostLoginEvent event) {
        JoinLeaveHandler.join(new SDCPlayer(event.getPlayer().getUniqueId(), event.getPlayer().getUsername()));
    }

    /**
     * The listener that runs on leave
     * @param event The leave event
     */
    @Subscribe
    public void onLeave(DisconnectEvent event) {
        JoinLeaveHandler.leave(SDCPlayer.get(event.getPlayer().getUniqueId()));
    }
}
