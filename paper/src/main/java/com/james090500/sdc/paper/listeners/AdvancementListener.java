package com.james090500.sdc.paper.listeners;

import com.james090500.sdc.common.api.SDCPlayer;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.handlers.AdvancementHandler;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
        if(Configs.getSettingsConfig().getAdvancement().isEnabled()) {
            //Set the variables
            String advancement = PlainTextComponentSerializer.plainText().serialize(event.getAdvancement().getDisplay().title());

            //Send to discord
            AdvancementHandler.send(SDCPlayer.get(event.getPlayer().getUniqueId()), advancement);
        }
    }
}
