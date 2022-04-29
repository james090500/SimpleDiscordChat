package com.james090500.sdc.common.commands;

import com.james090500.sdc.common.SimpleDiscordChat;

import java.util.UUID;

public class LinkingCommands {

    /**
     * The user visible logic for linking
     * @param uuid
     * @return
     */
    public String linkCommand(UUID uuid) {
        int code = SimpleDiscordChat.getInstance().getLinkHandler().generateCode(uuid);
        String response = String.format("Please direct message the bot (%s) the following code to link your account: %s", SimpleDiscordChat.getInstance().getBotUser().getName(), code);
        return response;
    }

    /**
     * The user visible logic for unlinking
     * @param uuid
     * @return
     */
    public String unlinkCommand(UUID uuid) {
        SimpleDiscordChat.getInstance().getLinkHandler().unlinkAccount(uuid);
        return "Your Minecraft and Discord account have been unlinked!";
    }
}
