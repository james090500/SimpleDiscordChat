package com.james090500.sdc.common.config;

import lombok.Getter;

import java.util.HashMap;

@Getter
public class SettingsConfig {

    public String botToken;
    public String chatChannel;

    public JDBC jdbc;
    @Getter public static class JDBC {
        public boolean enabled;
        public String url;
        public String username;
        public String password;
    }

    public String command;

    public Format format;
    @Getter public static class Format {
        public String minecraft;
        public String discord;
    }

    public EmbedFormat firstJoin;
    public EmbedFormat join;
    public EmbedFormat leave;
    public EmbedFormat advancement;
    @Getter public static class EmbedFormat {
        public boolean enabled;
        public int color;
        public String message;
    }

    public boolean linking;
    public boolean syncGroups;
    public boolean syncUsernames;

    public Syncing syncing;
    @Getter public static class Syncing {
        public HashMap<String, String> groups;
        public String username;
    }
}
