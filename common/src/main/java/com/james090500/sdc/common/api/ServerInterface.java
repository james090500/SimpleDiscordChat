package com.james090500.sdc.common.api;

import java.util.UUID;

public interface ServerInterface {

    void onDiscordMessage(String username, String message);

    String parsePlaceholders(UUID uuid, String message);
}
