package com.james090500.sdc.velocity;

import com.google.inject.Inject;
import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.api.SDCPlayer;
import com.james090500.sdc.common.commands.CommandManager;
import com.james090500.sdc.common.handlers.JoinLeaveHandler;
import com.james090500.sdc.common.handlers.SyncHandler;
import com.james090500.sdc.velocity.helpers.ChatControlHelper;
import com.james090500.sdc.velocity.listeners.ChatListener;
import com.james090500.sdc.velocity.listeners.JoinLeaveListener;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "simplediscordchat", name = "SimpleDiscordChat", version = "0.0.1", description = "A simple discord chat", authors = {"james095000"}, dependencies = {@Dependency(id = "luckperms")})
public class SDCVelocity {

    @Getter private final ProxyServer server;
    @Getter private final Logger logger;
    @Getter private final Path dataDirectory;
    @Getter private static SDCVelocity instance;
    @Getter private LuckPerms luckPermsApi;

    @Inject
    public SDCVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        //Permissions
        this.luckPermsApi = LuckPermsProvider.get();

        //Start the common enable
        SimpleDiscordChat.getInstance().onEnable(logger, dataDirectory.toFile(), new Server());

        //Register listeners
        server.getEventManager().register(this, new ChatListener());
        server.getEventManager().register(this, new JoinLeaveListener());

        //Commands
        final CommandManager commandManager = new CommandManager();
        server.getCommandManager().register(server.getCommandManager().metaBuilder("discord").build(), (SimpleCommand) invocation -> {
            String response;
            if (invocation.source() instanceof Player) {
                response = commandManager.init(((Player) invocation.source()).getUniqueId(), invocation.arguments());
            } else {
                response = commandManager.init(null, invocation.arguments());
            }
            invocation.source().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(response));
        });

        //Syncs
        server.getScheduler().buildTask(this, () -> server.getAllPlayers().forEach(player -> {
                SDCPlayer sdcPlayer = SDCPlayer.get(player.getUniqueId());
                sdcPlayer.setDisplayName(ChatControlHelper.getNick(player.getUsername()));
                String primaryGroup = getLuckPermsApi().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup();
                SyncHandler.doSync(sdcPlayer, primaryGroup);
            }
        )).repeat(5, TimeUnit.MINUTES).schedule();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        server.getAllPlayers().forEach(player -> JoinLeaveHandler.leave(SDCPlayer.get(player.getUniqueId())));
        SimpleDiscordChat.getInstance().onDisable();
    }
}
