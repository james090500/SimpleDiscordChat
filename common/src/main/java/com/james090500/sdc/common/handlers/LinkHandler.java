package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;

public class LinkHandler {

    /**
     * Handle a link message
     * @param user The author of the message
     * @param message The message contents (We already check if empty or bot before this)
     */
    public static void handle(User user, Message message) {
        String code = message.getContentRaw();
        PrivateChannel channel = message.getPrivateChannel();

        //Ignore if linking is disabled
        if(!SimpleDiscordChat.getInstance().getConfigs().getSettingsConfig().isLinking()) return;

        //Check the code is correct length
        if(code.length() != 4) {
            channel.sendMessage("That code is incorrect, please double check and try again.").queue();
            return;
        }

        //Check the code is numeric
        try {
            Integer.parseInt(code);
        } catch(NumberFormatException e) {
            channel.sendMessage("That code is incorrect, please double check and try again.").queue();;
            return;
        }

        //Valid code
        String format = String.format("Hello %s, thanks for sending the code %s", user.getName(), code);
        message.getPrivateChannel().sendMessage(format).queue();
    }
}
