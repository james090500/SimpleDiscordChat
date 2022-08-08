package com.james090500.sdc.common.api;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.UUID;

public class SDCPlayer {

    @Getter
    @Setter
    private UUID uuid;

    @Getter
    @Setter
    private String username;

    @Setter
    private String displayName;
    public String getDisplayName() {
        return displayName != null ? displayName : username;
    }

    @Getter
    @Setter
    private String discordSnowflake;

    private static HashMap<UUID, SDCPlayer> sdcPlayers = new HashMap<>();

    public SDCPlayer(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
        sdcPlayers.put(uuid, this);
    }

    public static SDCPlayer get(UUID uuid) {
        return sdcPlayers.get(uuid);
    }

    public static void remove(UUID uuid) {
        sdcPlayers.remove(uuid);
    }
}
