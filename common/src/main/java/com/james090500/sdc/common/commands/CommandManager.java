package com.james090500.sdc.common.commands;

import com.james090500.sdc.common.config.Configs;

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
        String defaultResponse = Configs.getSettingsConfig().getCommand();
        if(args.length >= 1 && uuid != null) {
            if(Configs.getSettingsConfig().isLinking()) {
                switch (args[0]) {
                    case "link":
                        return linkingCommands.linkCommand(uuid);
                    case "unlink":
                        return linkingCommands.unlinkCommand(uuid);
                    default:
                        return defaultResponse;
                }
            }
        }
        return defaultResponse;
    }
}
