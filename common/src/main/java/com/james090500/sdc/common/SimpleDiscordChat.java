package com.james090500.sdc.common;

import com.james090500.sdc.common.config.Configs;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class SimpleDiscordChat {

    //Public variables
    private static SimpleDiscordChat instance;
    @Getter private TextChannel chatChannel;

    //The JDA instance
    private JDA jda;

    //Needed intents
    private final EnumSet<GatewayIntent> intents = EnumSet.of(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.DIRECT_MESSAGES
    );

    /**
     * Enable the plugin
     */
    public void onEnable() {
        //Check vital config stuff
        if(Configs.getSettingsConfig().botToken == null || Configs.getSettingsConfig().chatChannel == null) {
            //Todo fatal error
            return;
        }

        //Build the JDA Config
        JDABuilder jdaBuilder = JDABuilder.create(intents)
                .setToken(Configs.getSettingsConfig().botToken);

        //Start the JDA Instance
        try {
            jda = jdaBuilder.build();

            chatChannel = jda.getTextChannelById(Configs.getSettingsConfig().chatChannel);
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disable the plugin
     */
    public void onDisable() {
        jda.shutdownNow();
    }

    /**
     * Return the instance of SimpleDiscordChat
     * @return SimpleDiscordChat
     */
    public static SimpleDiscordChat getInstance() {
        return instance == null ? instance = new SimpleDiscordChat() : instance;
    }
}
