package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.config.SQLHelper;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.UUID;

public class LinkHandler {

    /**
     * Handle a link message
     * @param user The author of the message
     * @param message The message contents (We already check if empty or bot before this)
     */
    public static void handle(User user, Message message) {
        String code = message.getContentRaw();
        PrivateChannel channel = message.getChannel().asPrivateChannel();

        //Ignore if linking is disabled
        if(!Configs.getSettingsConfig().isLinking()) return;

        //Check the code is correct length
        if(code.length() != 4) {
            channel.sendMessage("That code is incorrect, please double check and try again.").queue();
            return;
        }

        //Check the code is numeric
        int codeInt;
        try {
            codeInt = Integer.parseInt(code);
        } catch(NumberFormatException e) {
            channel.sendMessage("That code is incorrect, please double check and try again.").queue();;
            return;
        }

        //Valid code
        UUID uuid = SQLHelper.checkCode(codeInt);
        SQLHelper.forgetPlayer(uuid);
        if(uuid != null) {
            SQLHelper.updatePlayer(uuid, user.getId());
            String format = String.format("Hello %s, your discord account and minecraft account have been linked!", user.getName(), code);
            message.getChannel().asPrivateChannel().sendMessage(format).queue();
        } else {
            channel.sendMessage("That code is incorrect, please double check and try again.").queue();
        }
    }

    /**
     * Generate a new link code and return it for a uuid
     * This feels really dumb but I can't think of a better way atm so... todo
     * @param uuid The players uuid
     */
    public static int generateCode(UUID uuid) {
        int code = 0;
        boolean codeAddedToDatabase = false;
        while(!codeAddedToDatabase) {
            code = (int) (Math.random() * (9999 - 1000) + 1) + 1000;
            codeAddedToDatabase = SQLHelper.updateLinking(uuid, code);
        }
        return code;
    }

    /**
     * Unlink a users discord and minecraft account
     * @param uuid The player uuid
     */
    public static void unlinkAccount(UUID uuid) {
        SQLHelper.updatePlayer(uuid, null);
    }
}
