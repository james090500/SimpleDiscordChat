package com.james090500.sdc.common.config;

import lombok.Getter;

public class SettingsConfig {

    @Getter public String botToken;
    @Getter public String chatChannel;

    @Getter public Format format;
    public static class Format {
        @Getter public String minecraft;
        @Getter public String discord;
    }

    @Getter public EmbedFormat firstJoin;
    @Getter public EmbedFormat join;
    @Getter public EmbedFormat leave;
    @Getter public EmbedFormat advancement;

    public static class EmbedFormat {
        @Getter public boolean enabled;
        @Getter public int color;
        @Getter public String message;
    }

}
