package com.james090500.sdc.common.commands;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.handlers.LinkHandler;

import java.util.UUID;

public class LinkingCommands {

    /**
     * The user visible logic for linking
     * @param uuid
     * @return
     */
    public String linkCommand(UUID uuid) {
        int code = LinkHandler.generateCode(uuid);
        String response = String.format("Please direct message the bot (%s) the following code to link your account: %s", SimpleDiscordChat.getInstance().getBotUser().getName(), code);
        return response;
    }

    /**
     * The user visible logic for unlinking
     * @param uuid
     * @return
     */
    public String unlinkCommand(UUID uuid) {
        return "todo";
    }
}
