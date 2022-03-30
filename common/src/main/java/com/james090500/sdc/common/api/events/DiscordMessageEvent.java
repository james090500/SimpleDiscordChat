package com.james090500.sdc.common.api.events;

import com.james090500.sdc.common.api.Event;
import lombok.Getter;

public class DiscordMessageEvent extends Event {

    @Getter String authorName;
    @Getter String message;

    public DiscordMessageEvent(String name, String contentDisplay) {
        this.authorName = name;
        this.message = contentDisplay;
    }
}
