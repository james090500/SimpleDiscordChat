package com.james090500.sdc.common.commands;

import java.util.UUID;

public class CommandManager {

    private LinkingCommands linkingCommands = new LinkingCommands();

    /**
     * The command handler
     * @param uuid The players uuid
     * @param args The args (eg /discord {link/unlike} etc)
     * @return
     */
    public String init(UUID uuid, String[] args) {
        String defaultResponse = "This is the discord server discord.gg/capecraft TODO";
        if(args.length >= 1 && uuid != null) {
            switch(args[0]) {
                case "link":
                    return linkingCommands.linkCommand(uuid);
                case "unlink":
                    return linkingCommands.unlinkCommand(uuid);
                default:
                    return defaultResponse;
            }
        }
        return defaultResponse;
    }
}
