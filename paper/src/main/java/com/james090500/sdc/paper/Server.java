package com.james090500.sdc.paper;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.api.ServerInterface;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Server implements ServerInterface {

    @Override
    public void onDiscordMessage(String username, String message) {
        //Chat Format
        String chatFormat = SimpleDiscordChat.getInstance().getConfigs().getSettingsConfig().getFormat().getMinecraft();

        //Set the variables
        chatFormat = chatFormat.replaceAll("%username%", username);
        chatFormat = chatFormat.replaceAll("%message%", message);

        //The final chat message
        Component finalMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(chatFormat);
        Bukkit.getServer().sendMessage(finalMessage);
    }

    @Override
    public String parsePlaceholders(UUID uuid, String message) {
        Player player = Bukkit.getPlayer(uuid);
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
