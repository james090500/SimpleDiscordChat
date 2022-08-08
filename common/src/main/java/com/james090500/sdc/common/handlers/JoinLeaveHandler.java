package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.config.SQLHelper;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.UUID;

public class JoinLeaveHandler {

    /**
     * The join embed
     */
    public static void join(UUID uuid) {
        if(SimpleDiscordChat.getInstance().getChatChannel() == null) return;
        new Thread(() -> {
            String avatar = String.format(SimpleDiscordChat.getAvatar(), uuid, System.currentTimeMillis());

            //Start an embed
            EmbedBuilder joinEmbed = new EmbedBuilder();

            //Check if the player has already joined
            if(SQLHelper.getPlayer(uuid) == null) {
                //Lets register the player
                SQLHelper.updatePlayer(uuid, null);

                //Check if we want first join messages
                if(!Configs.getSettingsConfig().getFirstJoin().isEnabled()) return;

                //Add the first join colour
                joinEmbed.setColor(Configs.getSettingsConfig().getFirstJoin().getColor());

                //Build the message
                String message = Configs.getSettingsConfig().getFirstJoin().getMessage();
                message = SimpleDiscordChat.getInstance().getServerInterface().parsePlaceholders(uuid, message, true);
                joinEmbed.setAuthor(message, null, avatar);
            } else {
                //Check if we want regular join messages
                if(!Configs.getSettingsConfig().getJoin().isEnabled()) return;

                //Add the join colour
                joinEmbed.setColor(Configs.getSettingsConfig().getJoin().getColor());

                //Build the message
                String message = Configs.getSettingsConfig().getJoin().getMessage();
                message = SimpleDiscordChat.getInstance().getServerInterface().parsePlaceholders(uuid, message, true);
                joinEmbed.setAuthor(message, null, avatar);
            }

            //Send it to discord
            SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
        }).run();
    }

    /**
     * The leave embed
     */
    public static void leave(UUID uuid) {
        if(SimpleDiscordChat.getInstance().getChatChannel() == null) return;
        new Thread(() -> {
            String avatar = String.format(SimpleDiscordChat.getAvatar(), uuid, System.currentTimeMillis());

            //Forget the player
            SQLHelper.forgetPlayer(uuid);

            //Check if we want leave messages to show
            if (!Configs.getSettingsConfig().getLeave().isEnabled()) return;

            //Build the embed
            EmbedBuilder joinEmbed = new EmbedBuilder();
            joinEmbed.setColor(Configs.getSettingsConfig().getLeave().getColor());
            String message = Configs.getSettingsConfig().getLeave().getMessage();
            message = SimpleDiscordChat.getInstance().getServerInterface().parsePlaceholders(uuid, message, true);
            joinEmbed.setAuthor(message, null, avatar);

            //Send it to discord
            SimpleDiscordChat.getInstance().getChatChannel().sendMessageEmbeds(joinEmbed.build()).queue();
        }).run();
    }
}
