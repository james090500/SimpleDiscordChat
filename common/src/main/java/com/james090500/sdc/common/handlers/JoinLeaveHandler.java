package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
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

        joinEmbed.setColor(65280);
        joinEmbed.setAuthor(username + " has joined the server", null, avatar);

        SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
    }

    /**
     * The leave embed
     */
    public void leave() {
        EmbedBuilder joinEmbed = new EmbedBuilder();
        joinEmbed.setColor(16711680);
        joinEmbed.setAuthor(username + " has left the server", null, avatar);

        SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
    }
}
