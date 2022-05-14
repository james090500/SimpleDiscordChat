package com.james090500.sdc.velocity;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.api.ServerInterface;
import com.james090500.sdc.common.config.Configs;
import io.papermc.paper.text.PaperComponents;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.UUID;

public class Server implements ServerInterface {

    @Override
    public void onDiscordMessage(Color roleColor, String roleName, String username, String message) {
        //Chat Format
        String chatFormat = Configs.getSettingsConfig().getFormat().getMinecraft();

        SimpleDiscordChat.getInstance().getLogger().error(chatFormat);

        //Set the variables
        String hexColor = String.format("#%02x%02x%02x", roleColor.getRed(), roleColor.getGreen(), roleColor.getBlue());
        chatFormat = chatFormat.replaceAll("%role_color%", hexColor);
        chatFormat = chatFormat.replaceAll("%role_name%", roleName);
        chatFormat = chatFormat.replaceAll("%username%", username);
        chatFormat = chatFormat.replaceAll("%message%", message);

        SimpleDiscordChat.getInstance().getLogger().error(chatFormat);

        //The final chat message
        Component finalMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(chatFormat);
        Bukkit.getServer().sendMessage(finalMessage);
    }

    @Override
    public String parsePlaceholders(UUID uuid, String message, boolean clean) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        String placeholder = PlaceholderAPI.setPlaceholders(offlinePlayer, message);
        if(clean) {
            Component component = LegacyComponentSerializer.legacySection().deserialize(placeholder);
            return PaperComponents.plainTextSerializer().serialize(component);
        } else {
            return placeholder;
        }
    }

    @Override
    public void addBoostRank(UUID uuid, String groupName) {
        SDCPaper.getInstance().getPerms().playerAddGroup(Bukkit.getPlayer(uuid), groupName);
    }
}
