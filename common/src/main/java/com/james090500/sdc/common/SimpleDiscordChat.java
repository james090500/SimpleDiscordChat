package com.james090500.sdc.common;

import com.james090500.sdc.common.api.ServerInterface;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.config.SQLHelper;
import com.james090500.sdc.common.handlers.ChatHandler;
import com.james090500.sdc.common.listeners.MessageListener;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.EnumSet;

public class SimpleDiscordChat {

    //Public variables
    @Getter private static final String avatar = "https://minecraftapi.net/api/v2/profile/%s/avatar?size=128&overlay=true#%s";
    @Getter private static final SimpleDiscordChat instance = new SimpleDiscordChat();
    @Getter private ServerInterface serverInterface;
    @Getter private Logger logger;
    @Getter private File dataFolder;
    @Getter private SelfUser bot;
    @Getter private TextChannel chatChannel;

    //The JDA instance
    @Getter private JDA jda;

    //Needed intents
    private final EnumSet<GatewayIntent> activeIntents = EnumSet.of(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.MESSAGE_CONTENT
    );

    //Disabled caches
    private final EnumSet<CacheFlag> disabledCache = EnumSet.of(
            CacheFlag.ACTIVITY,
            CacheFlag.VOICE_STATE,
            CacheFlag.EMOJI,
            CacheFlag.STICKER,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS
    );

    /**
     * Enable the plugin
     */
    public void onEnable(Logger logger, File dataFolder, ServerInterface serverInterface) {
        this.logger = logger;
        this.serverInterface = serverInterface;
        this.dataFolder = dataFolder;

        //Check vital config stuff
        if(Configs.getSettingsConfig() == null || Configs.getSettingsConfig().getBotToken() == null || Configs.getSettingsConfig().getChatChannel() == null) {
            getLogger().error("Your config is missing important values (bot token, chat channel etc)");
            return;
        }

        //Avoid blocking whilst we load
        new Thread(() -> {
            //Build the JDA Config
            JDABuilder jdaBuilder = JDABuilder.create(activeIntents)
                    .disableCache(disabledCache)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setToken(Configs.getSettingsConfig().getBotToken());

            //Start the JDA Instance
            try {
                jda = jdaBuilder
                        .addEventListeners(new MessageListener())
                        .build();

                //Await ready, can we async this?
                jda.awaitReady();

                this.bot = jda.getSelfUser();
                this.chatChannel = jda.getTextChannelById(Configs.getSettingsConfig().getChatChannel());
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
            }

            //Start Message
            ChatHandler.sendMessage(":green_circle: **Server Started**");
        }).start();
    }

    /**
     * Disable the plugin
     */
    public void onDisable() {
        if(jda != null) {
            SQLHelper.close();
            ChatHandler.sendMessage(":red_circle: **Server Stopped**");
            jda.shutdown();
        }
    }
}
