package com.james090500.sdc.common.config;

public class SettingsConfig {

    public String botToken;
    public String chatChannel;

    public String formatMinecraft;
    public String formatDiscord;

    public class JoinLeaveFormat {
        public boolean enabled;
        public int color;
        public String message;
    }
    public JoinLeaveFormat firstJoin;
    public JoinLeaveFormat join;
    public JoinLeaveFormat leave;

}
