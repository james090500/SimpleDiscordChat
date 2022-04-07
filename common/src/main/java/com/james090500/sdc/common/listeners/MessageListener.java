package com.james090500.sdc.common.listeners;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.api.events.DiscordMessageEvent;
import com.james090500.sdc.common.config.Configs;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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
            message.getPrivateChannel().sendMessage("Hi there " + user.getName());
        } else if(event.getChannel().getId().equals(Configs.getSettingsConfig().chatChannel)) {
            SimpleDiscordChat.getInstance().callEvent(new DiscordMessageEvent(
                    user.getName(),
                    message.getContentDisplay()
            ));
        }
    }
}
