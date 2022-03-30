package com.james090500.sdc.common;

import com.james090500.sdc.common.api.Event;
import com.james090500.sdc.common.api.events.Subscribe;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.handlers.ChatHandler;
import com.james090500.sdc.common.helpers.ServerCache;
import com.james090500.sdc.common.listeners.MessageListener;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SimpleDiscordChat {

    //Public variables
    @Getter private static SimpleDiscordChat instance = new SimpleDiscordChat();
    @Getter private TextChannel chatChannel;

    //Private variables
    private List<Object> listeners = new ArrayList<>();

    //The JDA instance
    private JDA jda;

    //Needed intents
    private final EnumSet<GatewayIntent> activeIntents = EnumSet.of(
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.DIRECT_MESSAGES
    );

    //Disabled cache
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
    public void onEnable(File dataFolder) {
        //Set the plugin directory
        new ServerCache().init(dataFolder);

        //Check vital config stuff
        if(Configs.getSettingsConfig().botToken == null || Configs.getSettingsConfig().chatChannel == null) {
            System.out.println("Fatal error");
            return;
        }

        //Build the JDA Config
        JDABuilder jdaBuilder = JDABuilder.create(activeIntents)
                .disableCache(disabledCache)
                .setToken(Configs.getSettingsConfig().botToken);

        //Start the JDA Instance
        try {
            instance.jda = jdaBuilder
                    .addEventListeners(new MessageListener())
                    .build();

            //Await ready, can we async this?
            instance.jda.awaitReady();

            instance.chatChannel = instance.jda.getTextChannelById(Configs.getSettingsConfig().chatChannel);
        } catch (LoginException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Start Message
        new ChatHandler(":green_circle: **Server Started**");
    }

    /**
     * Disable the plugin
     */
    public void onDisable() {
        if(instance.jda != null) {
            new ChatHandler(":red_circle: **Server Stopped**");
            instance.jda.shutdown();
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

        instance.listeners.add(listener);
    }

    /**
     * Call the event
     * @param event
     */
    public <T extends Event> T callEvent(T event) {
        // Thanks your DiscordSRV for inspiration under GNU v3
        for(Object listener : instance.listeners) {
            for (Method method : listener.getClass().getMethods()) {
                if (method.getParameters().length != 1)
                    continue; // Listeners will only have a single paramter

                if (!method.getParameters()[0].getType().isAssignableFrom(event.getClass()))
                    continue; // Makes sure the event uses this event

                if (!method.isAnnotationPresent(Subscribe.class))
                    continue; // Makes sure the method has our subscribe annotation

                for (Annotation annotation : method.getAnnotations()) {
                    if (!(annotation instanceof Subscribe))
                        continue; // Loop all annotations until we get ours

                    try {
                        method.invoke(listener, event);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return event;
    }
}
