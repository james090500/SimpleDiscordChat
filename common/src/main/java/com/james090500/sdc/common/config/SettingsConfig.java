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

    @Getter public JoinLeaveFormat firstJoin;
    @Getter public JoinLeaveFormat join;
    @Getter public JoinLeaveFormat leave;
    public static class JoinLeaveFormat {
        @Getter public boolean enabled;
        @Getter public int color;
        @Getter public String message;
    }

}
