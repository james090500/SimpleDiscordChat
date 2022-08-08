package com.james090500.sdc.velocity;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.commands.CommandManager;
import com.james090500.sdc.common.handlers.JoinLeaveHandler;
import com.james090500.sdc.common.handlers.SyncHandler;
import com.james090500.sdc.velocity.listeners.AdvancementListener;
import com.james090500.sdc.velocity.listeners.ChatListener;
import com.james090500.sdc.velocity.listeners.JoinLeaveListener;
import lombok.Getter;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SDCPaper extends JavaPlugin {

    @Getter private static SDCPaper instance;
    @Getter private Permission perms;

    @Override
    public void onEnable() {
        //Setup vault
        perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();

        //Start the common enable
        SimpleDiscordChat.getInstance().onEnable(getSLF4JLogger(), getDataFolder(), new Server());

        //Register instance
        instance = this;

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
        long fiveMin = (20L * 60 * 5);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> Bukkit.getOnlinePlayers().forEach(player -> {
                String displayName = PlainTextComponentSerializer.plainText().serialize(player.displayName());
                SyncHandler.doSync(player.getUniqueId(), displayName, perms.getPrimaryGroup(player));
            }
        ), fiveMin, fiveMin);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> JoinLeaveHandler.leave(player.getUniqueId()));
        SimpleDiscordChat.getInstance().onDisable();
    }
}
