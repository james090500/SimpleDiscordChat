package com.james090500.sdc.common.config;

import com.james090500.sdc.common.SimpleDiscordChat;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.*;
import java.nio.file.Files;

public class Configs {

    @Getter private SettingsConfig settingsConfig;

    public Configs(File dataFolder) {
        //Makes plugin folders if they don't exist
        File pluginFolder = dataFolder;
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

        try (Reader reader = new FileReader(configFile)) {
            CustomClassLoaderConstructor customClassLoaderConstructor = new CustomClassLoaderConstructor(SettingsConfig.class.getClassLoader());
            settingsConfig = new Yaml(customClassLoaderConstructor).loadAs(reader, SettingsConfig.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
