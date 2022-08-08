package com.james090500.sdc.common.api;

import java.awt.*;
import java.util.UUID;

public interface ServerInterface {

    void onDiscordMessage(Color roleColor, String roleName, String username, String message);

    String parsePlaceholders(SDCPlayer sdcPlayer, String message, boolean clean);

    void addBoostRank(UUID uuid, String groupName);
}
