package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.config.SettingsConfig;
import com.james090500.sdc.common.helpers.ServerCache;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.util.UUID;

public class JoinLeaveHandler {

    private String avatar;
    private UUID uuid;
    private String username;

    public JoinLeaveHandler(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;

        this.avatar = String.format("https://minecraftapi.net/api/v1/profile/%s/avatar?size=128&overlay=true#%s", uuid, System.currentTimeMillis());
    }

    /**
     * The join embed
     */
    public void join() {
        EmbedBuilder joinEmbed = new EmbedBuilder();

        if(!ServerCache.getInstance().isPlayerRegistered(uuid)) {
            ServerCache.getInstance().registerPlayer(uuid);
            if(!Configs.getSettingsConfig().firstJoin.enabled) return;

            joinEmbed.setColor(Configs.getSettingsConfig().firstJoin.color);
            joinEmbed.setAuthor("\uD83C\uDF89 " + username + " has joined the server for the first time! \uD83C\uDF89", null, avatar);
        } else {
            if(!Configs.getSettingsConfig().join.enabled) return;

            joinEmbed.setColor(Configs.getSettingsConfig().join.color);
            joinEmbed.setAuthor(username + " has joined the server", null, avatar);
        }

        SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
    }

    /**
     * The leave embed
     */
    public void leave() {
        if(!Configs.getSettingsConfig().leave.enabled) return;

        EmbedBuilder joinEmbed = new EmbedBuilder();
        joinEmbed.setColor(Configs.getSettingsConfig().leave.color);
        joinEmbed.setAuthor(username + " has left the server", null, avatar);

        SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
    }
}
