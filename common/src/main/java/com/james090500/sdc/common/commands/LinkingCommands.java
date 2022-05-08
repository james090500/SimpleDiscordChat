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
        String response = String.format("Please direct message the bot (%s) the following code to link your account: %s", SimpleDiscordChat.getInstance().getBot().getName(), code);
        return response;
    }

    /**
     * The user visible logic for unlinking
     * @param uuid
     * @return
     */
    public String unlinkCommand(UUID uuid) {
        LinkHandler.unlinkAccount(uuid);
        return "Your Minecraft and Discord account have been unlinked!";
    }
}
