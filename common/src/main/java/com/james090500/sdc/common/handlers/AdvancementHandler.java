package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.SettingsConfig;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.UUID;

public class AdvancementHandler {
    private final String avatar;
    private final UUID uuid;
    private final String username;

    /**
     * The construct for the join leave handler
     * @param uuid The users uuid
     * @param username The users username
     */
    public AdvancementHandler(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;

        this.avatar = String.format(SimpleDiscordChat.AVATAR, uuid, System.currentTimeMillis());
    }

    /**
     * Send the advancement
     */
    public void send(String advancement) {
        //Check if we want leave messages to show
        SettingsConfig settingsConfig = SimpleDiscordChat.getInstance().getConfigs().getSettingsConfig();
        if(!settingsConfig.getAdvancement().isEnabled()) return;

        //Build the embed
        EmbedBuilder advancementEmbed = new EmbedBuilder();
        advancementEmbed.setColor(settingsConfig.getAdvancement().getColor());
        String message = settingsConfig.getAdvancement().getMessage().replaceAll("%username%", username); //todo placeholder this
        message = message.replaceAll("%advancement%", advancement); //todo placeholder this
        advancementEmbed.setAuthor(message, null, avatar);

        //Send it to discord
        SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(advancementEmbed.build()).queue();
    }
}
