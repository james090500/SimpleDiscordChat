package com.james090500.sdc.common.listeners;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.api.events.DiscordMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    /**
     * The listener for incoming discord messages
     * @param event discord message event
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //Ignore bot messages
        if(event.getAuthor().isBot()) return;

        //Ignore attachments
        if(!event.getMessage().getAttachments().isEmpty()) return;

        //Ignore empty messages
        if(event.getMessage().getContentDisplay().isEmpty()) return;

        SimpleDiscordChat.getInstance().callEvent(new DiscordMessageEvent(
                event.getAuthor().getName(),
                event.getMessage().getContentDisplay()
        ));
    }
}
