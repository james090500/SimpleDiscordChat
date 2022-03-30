package com.james090500.sdc.common.listeners;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.api.events.DiscordMessageEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;

        SimpleDiscordChat.getInstance().callEvent(new DiscordMessageEvent(
                event.getAuthor().getName(),
                event.getMessage().getContentDisplay()
        ));
    }
}
