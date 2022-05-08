package com.james090500.sdc.paper.listeners;

import com.james090500.sdc.common.handlers.AdvancementHandler;
import io.papermc.paper.text.PaperComponents;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class AdvancementListener implements Listener {

    /**
     * Convert the advancement to basic english
     * @param event The players advancement
     */
    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        //Get the player
        Player player = event.getPlayer();

        //Set the variables
        String advancement = PaperComponents.plainTextSerializer().serialize(event.getAdvancement().getDisplay().title());

        //Send to discord
        AdvancementHandler.send(player.getUniqueId(), advancement);
    }
}
