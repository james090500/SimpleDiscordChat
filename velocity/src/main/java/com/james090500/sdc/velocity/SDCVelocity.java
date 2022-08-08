package com.james090500.sdc.velocity;

import com.google.inject.Inject;
import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.commands.CommandManager;
import com.james090500.sdc.common.handlers.JoinLeaveHandler;
import com.james090500.sdc.common.handlers.SyncHandler;
import com.james090500.sdc.velocity.listeners.AdvancementListener;
import com.james090500.sdc.velocity.listeners.ChatListener;
import com.james090500.sdc.velocity.listeners.JoinLeaveListener;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Plugin(id = "simplediscordchat", name = "SimpleDiscordChat", version = "0.0.1", description = "A simple discord chat", authors = { "james095000" })
public class SDCVelocity {

    @Getter private final ProxyServer server;
    @Getter private final Logger logger;
    @Getter private final Path dataDirectory;

    @Inject
    public SDCVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        //Setup vault
        perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();

        //Start the common enable
        SimpleDiscordChat.getInstance().onEnable(logger, dataDirectory.toFile(), new Server());

        //Register listeners
        getServer().getPluginManager().registerEvents(new AdvancementListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);

        //Commands
        CommandManager commandManager = new CommandManager();
        getCommand("discord").setExecutor((sender, command, label, args) -> {
            String response;
            if(sender instanceof Player) {
                response = commandManager.init(((Player) sender).getUniqueId(), args);
            } else {
                response = commandManager.init(null, args);
            }
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(response));
            return true;
        });

        //Syncs
        server.getScheduler().buildTask(this, () -> server.getAllPlayers().forEach(player -> {
                    String displayName = player.getGameProfile().getName();
                    SyncHandler.doSync(player.getUniqueId(), displayName, perms.getPrimaryGroup(player));
                }
        )).repeat(5, TimeUnit.MINUTES).schedule();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        server.getAllPlayers().forEach(player -> JoinLeaveHandler.leave(player.getUniqueId()));
        SimpleDiscordChat.getInstance().onDisable();
    }
}
