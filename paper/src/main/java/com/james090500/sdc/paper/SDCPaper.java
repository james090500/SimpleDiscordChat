package com.james090500.sdc.paper;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.paper.config.Config;
import com.james090500.sdc.paper.listeners.ChatListener;
import com.james090500.sdc.paper.listeners.JoinLeaveListener;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SDCPaper extends JavaPlugin {

    @Getter private static SDCPaper instance;

    @Override
    public void onEnable() {
        //Register instance
        instance = this;

        //Load Config
        Config.loadConfig();

        //Register listeners
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(), this);
        SimpleDiscordChat.getInstance().registerListener(new ChatListener());

        //Start the common enable
        SimpleDiscordChat.getInstance().onEnable();

        //Commands
    }

    @Override
    public void onDisable() {
        SimpleDiscordChat.getInstance().onDisable();
    }
}
