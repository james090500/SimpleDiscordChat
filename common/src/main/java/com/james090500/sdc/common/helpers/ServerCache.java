package com.james090500.sdc.common.helpers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * The data.db file storing various data information
 */
public final class ServerCache {

    private final Gson GSON = new Gson();
    private Path jsonPath;

    @Getter private static ServerCache instance;

    /**
     * A list of players who got "caught" up by this plugin,
     * used for first join messages.
     */
    private Set<UUID> registeredPlayers = new HashSet<>();

    /**
     * Load the file
     * @param dataFolder The plugin working directory
     */
    public void init(File dataFolder) {
        instance = this;
        this.jsonPath = Path.of(dataFolder + "/users.json");

        try {
            //Make file if it doesn't exist
            if(!jsonPath.toFile().exists()) {
                Writer writer = new FileWriter(jsonPath.toFile());
                GSON.toJson(registeredPlayers, writer);
                writer.close();
            }

            //Load file to list
            Reader reader = new FileReader(jsonPath.toFile());
            this.registeredPlayers = new Gson().fromJson(reader, new TypeToken<HashSet<UUID>>(){}.getType());
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Register the player as "played" on the server
     *
     * @param uuid The players uuid
     */
    public void registerPlayer(final UUID uuid) {
        this.registeredPlayers.add(uuid);
        try {
            Writer writer = new FileWriter(jsonPath.toFile());
            GSON.toJson(this.registeredPlayers, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Is the player registered in our users.json file yet?
     *
     * @param uuid The players uuid
     * @return Whether the player is registered
     */
    public boolean isPlayerRegistered(UUID uuid) {
        return registeredPlayers.contains(uuid);
    }
}