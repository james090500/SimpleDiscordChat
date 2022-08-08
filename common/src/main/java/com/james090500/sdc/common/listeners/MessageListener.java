package com.james090500.sdc.common.listeners;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.handlers.LinkHandler;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Optional;

public class MessageListener extends ListenerAdapter {

    /**
     * The listener for incoming discord messages
     * @param event discord message event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        User user = event.getAuthor();
        Message message = event.getMessage();

        //Ignore bot messages
        if(user.isBot()) return;

        //Ignore attachments
        if(!message.getAttachments().isEmpty()) return;

        //Ignore empty messages
        if(message.getContentDisplay().isEmpty()) return;

        //Handle DM vs Guide and make sure its the right channel
        if(event.isFromType(ChannelType.PRIVATE)) {
            LinkHandler.handle(user, message);
        } else if(event.getChannel().getId().equals(Configs.getSettingsConfig().getChatChannel())) {
            Guild currentGuild = SimpleDiscordChat.getInstance().getChatChannel().getGuild();
            Member member = currentGuild.getMemberById(user.getId());

            //Make sure the user has a role
            Color roleColour = new Color(Integer.parseInt("36393E",16));
            String roleName = "";
            if(member.getRoles().size() > 0) {
                Role topRole = member.getRoles().get(0);
                roleColour = Optional.ofNullable(topRole.getColor()).orElse(roleColour);
                roleName = topRole.getName();
            }

            SimpleDiscordChat.getInstance().getServerInterface().onDiscordMessage(
                    roleColour,
                    roleName,
                    member.getEffectiveName(),
                    message.getContentDisplay()
            );
        }
    }
}
