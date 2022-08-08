package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.config.SettingsConfig;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.UUID;

public class AdvancementHandler {

    /**
     * Send the advancement
     */
    public static void send(UUID uuid, String advancement) {
        String avatar = String.format(SimpleDiscordChat.getAvatar(), uuid, System.currentTimeMillis());

        //Check if we want leave messages to show
        SettingsConfig settingsConfig = Configs.getSettingsConfig();
        if(!settingsConfig.getAdvancement().isEnabled()) return;

        //Build the embed
        EmbedBuilder advancementEmbed = new EmbedBuilder();
        advancementEmbed.setColor(settingsConfig.getAdvancement().getColor());
        String message = settingsConfig.getAdvancement().getMessage().replaceAll("%advancement%", advancement);
        message = SimpleDiscordChat.getInstance().getServerInterface().parsePlaceholders(uuid, message, true);
        advancementEmbed.setAuthor(message, null, avatar);

        //Send it to discord
        SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(advancementEmbed.build()).queue();
    }
}
