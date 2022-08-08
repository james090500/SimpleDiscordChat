package com.james090500.sdc.velocity;

import com.james090500.sdc.common.api.SDCPlayer;
import com.james090500.sdc.common.api.ServerInterface;
import com.james090500.sdc.common.config.Configs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

import java.awt.*;
import java.util.UUID;

public class Server implements ServerInterface {

    @Override
    public void onDiscordMessage(Color roleColor, String roleName, String username, String message) {
        //Chat Format
        String chatFormat = Configs.getSettingsConfig().getFormat().getMinecraft();

        //Set the variables
        String hexColor = String.format("#%02x%02x%02x", roleColor.getRed(), roleColor.getGreen(), roleColor.getBlue());
        chatFormat = chatFormat.replaceAll("%role_color%", hexColor);
        chatFormat = chatFormat.replaceAll("%role_name%", roleName);
        chatFormat = chatFormat.replaceAll("%username%", username);
        chatFormat = chatFormat.replaceAll("%message%", message);

        //The final chat message
        Component finalMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(chatFormat);

        SDCVelocity.getInstance().getServer().sendMessage(finalMessage);
    }

    @Override
    public String parsePlaceholders(SDCPlayer sdcPlayer, String message, boolean clean) {
        String primaryGroup = SDCVelocity.getInstance().getLuckPermsApi().getUserManager().loadUser(sdcPlayer.getUuid()).join().getPrimaryGroup();

        String placeholder = message;
        placeholder = placeholder.replaceAll("%player_name%", sdcPlayer.getUsername());
        placeholder = placeholder.replaceAll("%player_displayname%", sdcPlayer.getDisplayName());
        placeholder = placeholder.replaceAll("%player_group%", primaryGroup);

        if(clean) {
            Component component = LegacyComponentSerializer.legacySection().deserialize(placeholder);
            return PlainTextComponentSerializer.plainText().serialize(component);
        } else {
            return placeholder;
        }
    }

    @Override
    public void addBoostRank(UUID uuid, String groupName) {
        SDCVelocity.getInstance().getLuckPermsApi().getUserManager().getUser(uuid).setPrimaryGroup(groupName);
    }
}
