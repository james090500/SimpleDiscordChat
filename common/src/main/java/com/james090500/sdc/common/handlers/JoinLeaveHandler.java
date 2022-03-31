package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.helpers.ServerCache;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.UUID;

public class JoinLeaveHandler {

    private String avatar;
    private UUID uuid;
    private String username;

    /**
     * The construct for the join leave handler
     * @param uuid The users uuid
     * @param username The users username
     */
    public JoinLeaveHandler(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;

        this.avatar = String.format("https://minecraftapi.net/api/v1/profile/%s/avatar?size=128&overlay=true#%s", uuid, System.currentTimeMillis());
    }

    /**
     * The join embed
     */
    public void join() {
        //Start an embed
        EmbedBuilder joinEmbed = new EmbedBuilder();

        //Check if the player has already joined
        if(!ServerCache.getInstance().isPlayerRegistered(uuid)) {
            //Lets register the player
            ServerCache.getInstance().registerPlayer(uuid);

            //Check if we want first join messages
            if(!Configs.getSettingsConfig().getFirstJoin().isEnabled()) return;

            //Build the first join message
            joinEmbed.setColor(Configs.getSettingsConfig().getFirstJoin().getColor());

            //Build the message
            String message = Configs.getSettingsConfig().getFirstJoin().getMessage().replaceAll("%username%", username);
            joinEmbed.setAuthor(message, null, avatar);
        } else {
            //Check if we want regular join messages
            if(!Configs.getSettingsConfig().getFirstJoin().isEnabled()) return;

            String message = Configs.getSettingsConfig().getFirstJoin().getMessage().replaceAll("%username%", username);
            joinEmbed.setAuthor(message, null, avatar);
        }

        //Send it to discord
        SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
    }

    /**
     * The leave embed
     */
    public void leave() {
        //Check if we want leave messages to show
        if(!Configs.getSettingsConfig().getLeave().isEnabled()) return;

        //Build the embed
        EmbedBuilder joinEmbed = new EmbedBuilder();
        joinEmbed.setColor(Configs.getSettingsConfig().getLeave().getColor());
        String message = Configs.getSettingsConfig().getLeave().getMessage().replaceAll("%username%", username);
        joinEmbed.setAuthor(message, null, avatar);

        //Send it to discord
        SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
    }
}
