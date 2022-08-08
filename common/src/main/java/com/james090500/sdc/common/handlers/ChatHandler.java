package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;

public class ChatHandler {

    /**
     * Send a chat message to discord
     * @param message The formatted final message
     */
    public static void sendMessage(String message) {
        SimpleDiscordChat.getInstance().getChatChannel().sendMessage(message).queue();
    }

}
