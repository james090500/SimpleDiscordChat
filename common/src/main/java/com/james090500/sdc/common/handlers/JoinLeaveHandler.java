package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.SettingsConfig;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.UUID;

public class JoinLeaveHandler {

    private final SettingsConfig settingsConfig = SimpleDiscordChat.getInstance().getConfigs().getSettingsConfig();
    private final String avatar;
    private final UUID uuid;
    private final String username;

    /**
     * The construct for the join leave handler
     * @param uuid The users uuid
     * @param username The users username
     */
    public JoinLeaveHandler(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;

        this.avatar = String.format(SimpleDiscordChat.AVATAR, uuid, System.currentTimeMillis());
    }

    /**
     * The join embed
     */
    public void join() {
        new Thread(() -> {
            //Start an embed
            EmbedBuilder joinEmbed = new EmbedBuilder();

            //Check if the player has already joined
            if(SimpleDiscordChat.getInstance().getSqlHelper().getPlayer(uuid) == null) {
                //Lets register the player
                SimpleDiscordChat.getInstance().getSqlHelper().updatePlayer(uuid, null);

                //Check if we want first join messages
                if(!settingsConfig.getFirstJoin().isEnabled()) return;

                //Add the first join colour
                joinEmbed.setColor(settingsConfig.getFirstJoin().getColor());

                //Build the message
                String message = settingsConfig.getFirstJoin().getMessage().replaceAll("%username%", username);
                joinEmbed.setAuthor(message, null, avatar);
            } else {
                //Check if we want regular join messages
                if(!settingsConfig.getJoin().isEnabled()) return;

                //Add the join colour
                joinEmbed.setColor(settingsConfig.getJoin().getColor());

                //Build the message
                String message = settingsConfig.getJoin().getMessage().replaceAll("%username%", username);
                joinEmbed.setAuthor(message, null, avatar);
            }

            //Send it to discord
            SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
        }).run();
    }

    /**
     * The leave embed
     */
    public void leave() {
        new Thread(() -> {
            //Forget the player
            SimpleDiscordChat.getInstance().getSqlHelper().forgetPlayer(uuid);

            //Check if we want leave messages to show
            if (!settingsConfig.getLeave().isEnabled()) return;

            //Build the embed
            EmbedBuilder joinEmbed = new EmbedBuilder();
            joinEmbed.setColor(settingsConfig.getLeave().getColor());
            String message = settingsConfig.getLeave().getMessage().replaceAll("%username%", username);
            joinEmbed.setAuthor(message, null, avatar);

            //Send it to discord
            SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
        }).run();
    }
}
