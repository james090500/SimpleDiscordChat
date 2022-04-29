package com.james090500.sdc.common;

import com.james090500.sdc.common.api.Event;
import com.james090500.sdc.common.api.events.Subscribe;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.config.SQLHelper;
import com.james090500.sdc.common.handlers.ChatHandler;
import com.james090500.sdc.common.handlers.LinkHandler;
import com.james090500.sdc.common.handlers.SyncHandler;
import com.james090500.sdc.common.listeners.MessageListener;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SimpleDiscordChat {

    //Public variables
    public static final String AVATAR = "https://minecraftapi.net/api/v2/profile/%s/avatar?size=128&overlay=true#%s";
    @Getter private static final SimpleDiscordChat instance = new SimpleDiscordChat();
    @Getter private TextChannel chatChannel;
    @Getter private SelfUser botUser;
    @Getter private Configs configs;
    @Getter private SQLHelper sqlHelper;
    @Getter private Logger logger;
    @Getter private LinkHandler linkHandler;
    @Getter private SyncHandler syncHandler;

    //Private variables
    private final List<Object> listeners = new ArrayList<>();

    //The JDA instance
    private JDA jda;

    //Needed intents
    private final EnumSet<GatewayIntent> activeIntents = EnumSet.of(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.DIRECT_MESSAGES
    );

    //Disabled caches
    private final EnumSet<CacheFlag> disabledCache = EnumSet.of(
            CacheFlag.ACTIVITY,
            CacheFlag.VOICE_STATE,
            CacheFlag.EMOTE,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS
    );

    /**
     * Enable the plugin
     */
    public void onEnable(Logger logger, File dataFolder) {
        this.logger = logger;
        this.configs = new Configs(dataFolder);
        this.sqlHelper = new SQLHelper(dataFolder);
        this.linkHandler = new LinkHandler();
        this.syncHandler = new SyncHandler();

        //Check vital config stuff
        if(configs.getSettingsConfig() == null || configs.getSettingsConfig().getBotToken() == null || configs.getSettingsConfig().getChatChannel() == null) {
            getLogger().error("Your config is missing important values (bot token, chat channel etc)");
            return;
        }

        //Avoid blocking whilst we load
        new Thread(() -> {
            //Build the JDA Config
            JDABuilder jdaBuilder = JDABuilder.create(activeIntents)
                    .disableCache(disabledCache)
                    .setToken(configs.getSettingsConfig().getBotToken());

            //Start the JDA Instance
            try {
                jda = jdaBuilder
                        .addEventListeners(new MessageListener())
                        .build();

                //Await ready, can we async this?
                jda.awaitReady();
                botUser = jda.getSelfUser();

                chatChannel = jda.getTextChannelById(configs.getSettingsConfig().getChatChannel());
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
            sqlHelper.close();
            ChatHandler.sendMessage(":red_circle: **Server Stopped**");
            jda.shutdown();
        }
    }

    /**
     * Register an API listener
     * @param listener The listener
     */
    public void registerListener(Object listener) {
        //Make sure there's a subscribe event otherwise there's nothing to call
        int subbedMethods = 0;
        for(Method method : listener.getClass().getMethods()) {
            if(method.isAnnotationPresent(Subscribe.class)) {
                subbedMethods++;
            }
        }

        if(subbedMethods == 0) {
            throw new RuntimeException(listener.getClass().getName() + " missing public @Subscribe methods");
        }

        listeners.add(listener);
    }

    /**
     * Call the event
     * @param event The event to be called
     */
    public <T extends Event> T callEvent(T event) {
        // Thanks your DiscordSRV for inspiration under GNU v3
        for(Object listener : listeners) {
            for (Method method : listener.getClass().getMethods()) {
                if (method.getParameters().length != 1)
                    continue; // Listeners will only have a single parameter

                if (!method.getParameters()[0].getType().isAssignableFrom(event.getClass()))
                    continue; // Makes sure the event uses this event

                if (!method.isAnnotationPresent(Subscribe.class))
                    continue; // Makes sure the method has our subscribe annotation

                for (Annotation annotation : method.getAnnotations()) {
                    if (!(annotation instanceof Subscribe))
                        continue; // Loop all annotations until we get ours

                    try {
                        method.invoke(listener, event);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return event;
    }
}
