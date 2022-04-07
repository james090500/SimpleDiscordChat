package com.james090500.sdc.common.config;

import lombok.Getter;

@Getter
public class SettingsConfig {

    public String botToken;
    public String chatChannel;

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

}
