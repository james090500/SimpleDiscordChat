package com.james090500.sdc.common.config;

import com.james090500.sdc.common.SimpleDiscordChat;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Configs {

    @Getter private static SettingsConfig settingsConfig;

    //Runs first once
    static {
        //Makes plugin folders if they don't exist
        File pluginFolder = SimpleDiscordChat.getInstance().getDataFolder();
        if(!pluginFolder.exists()) {
            pluginFolder.mkdir();
        }

        //Copy default config if it doesn't exist
        File configFile = new File(pluginFolder, "settings.yml");
        if (!configFile.exists()) {
            try (InputStream in = SimpleDiscordChat.getInstance().getClass().getResourceAsStream("settings.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Reader reader = new FileReader(configFile, StandardCharsets.UTF_8)) {
            CustomClassLoaderConstructor customClassLoaderConstructor = new CustomClassLoaderConstructor(SettingsConfig.class.getClassLoader());
            settingsConfig = new Yaml(customClassLoaderConstructor).loadAs(reader, SettingsConfig.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
