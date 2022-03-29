package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;

public class ChatHandler {

    /**
     * Send a chat message to discord
     * @param message The formatted final message
     */
    public ChatHandler(String message) {
        SimpleDiscordChat.getInstance().getChatChannel().sendMessage(message).queue();
    }

}
