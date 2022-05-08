package com.james090500.sdc.common.api;

import java.awt.*;
import java.util.UUID;

public interface ServerInterface {

    void onDiscordMessage(Color roleColor, String roleName, String username, String message);

    String parsePlaceholders(UUID uuid, String message, boolean clean);
}
