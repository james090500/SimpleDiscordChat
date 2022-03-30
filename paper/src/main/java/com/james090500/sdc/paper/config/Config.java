package com.james090500.sdc.paper.config;

import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.config.SettingsConfig;
import com.james090500.sdc.paper.SDCPaper;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Config {

    /**
     * Initialise the config
     */
    public static void loadConfig() {
        //Makes plugin folders if they don't exist
        File pluginFolder = SDCPaper.getInstance().getDataFolder();
        if(!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }

        //Copy default config if it doesn't exist
        File configFile = new File(pluginFolder, "settings.yml");
        if (!configFile.exists()) {
            try (InputStream in = SDCPaper.getInstance().getResource("settings.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Configs.setSettingsConfig(stuff);
        YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(configFile);
        //Tempoary filler
        SettingsConfig settingsConfig = new SettingsConfig();
        settingsConfig.botToken = yamlConfig.getString("botToken");
        settingsConfig.chatChannel = yamlConfig.getString("chatChannel");
        Configs.setSettingsConfig(settingsConfig);
    }
}