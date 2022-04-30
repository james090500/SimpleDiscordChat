package com.james090500.sdc.common.config;

import com.james090500.sdc.common.SimpleDiscordChat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class SQLHelper {

    private Connection connection;
    private HashMap<UUID, UserInfo> userCache = new HashMap<>();

    /**
     * Initiate the SQLite instance
     * @param dataFolder Plugin folder
     */
    public SQLHelper(File dataFolder) {
        File sqlFile = new File(dataFolder, "users.db");
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + sqlFile.getAbsolutePath());
            if(connection == null) {
                SimpleDiscordChat.getInstance().getLogger().error("Failed to connect to SQLite database. The plugin will now disable");
                return;
            }

            String createUsersTable = "CREATE TABLE IF not EXISTS users (id integer PRIMARY KEY,uuid VARCHAR(36) NOT NULL,discord_snowflake VARCHAR(36) NULL)";
            String createLinkingTable = "CREATE TABLE IF not EXISTS linking (id integer PRIMARY KEY,uuid VARCHAR(36) NOT NULL,code VARCHAR(4) UNIQUE, created_at VARCHAR(10) NOT NULL)";

            //Statement
            Statement statement = connection.createStatement();
            statement.execute(createUsersTable);
            statement.execute(createLinkingTable);

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get players info from the database
     * @param uuid Player uuid
     * @return The players info
     */
    public UserInfo getPlayer(UUID uuid) {
        //Return from the cache!
        UserInfo cachedPlayer = userCache.get(uuid);
        if(cachedPlayer != null) return cachedPlayer;

        try {
            //Prepare an SQL Statement and then execute it
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE uuid = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            //Check results aren't empty
            while(resultSet.next()) {
                //Create a new user info instance and return the data
                UserInfo userInfo = new UserInfo(uuid, resultSet.getString(3));
                userCache.put(uuid, userInfo);
                return userInfo;
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Will update the player in the database
     * @param uuid Players uuid
     * @param discordSnowflake Discord snowflake but can be null if not linked
     */
    public void updatePlayer(UUID uuid, @Nullable String discordSnowflake) {
        forgetPlayer(uuid);
        try {
            //Remove duplicates
            PreparedStatement deleteOldPlayer = connection.prepareStatement("DELETE FROM users WHERE uuid = ?");
            deleteOldPlayer.setString(1, uuid.toString());
            deleteOldPlayer.execute();
            PreparedStatement deleteOldDiscord = connection.prepareStatement("DELETE FROM users WHERE discord_snowflake = ?");
            deleteOldDiscord.setString(1, discordSnowflake);
            deleteOldDiscord.execute();

            //Insert
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO users (uuid, discord_snowflake) VALUES(?,?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, discordSnowflake);

            UserInfo userInfo = new UserInfo(uuid, discordSnowflake);
            userCache.put(uuid, userInfo);

            preparedStatement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove the user from the map for memory reasons
     * @param uuid
     */
    public void forgetPlayer(UUID uuid) {
        userCache.remove(uuid);
    }

    /**
     * Insert a new linking code for a user and remove all old ones
     * @param uuid The users uuid
     * @param code The code to insert
     * @return
     */
    public boolean updateLinking(UUID uuid, int code) {
        forgetPlayer(uuid);
        long currentTime = System.currentTimeMillis() / 1000;
        try {
            //Prepare an SQL Statement and then execute it
            PreparedStatement checkCodeStatement = connection.prepareStatement("SELECT * FROM linking WHERE code = ?");
            checkCodeStatement.setString(1, String.valueOf(code));
            ResultSet resultSet = checkCodeStatement.executeQuery();

            //Check if the results are empty and if they need a new code
            while(resultSet.next()) {
                long createdAt = Long.parseLong(resultSet.getString(3));
                if((currentTime - createdAt) > 300) {
                    PreparedStatement deleteOldCode = connection.prepareStatement("DELETE FROM linking WHERE code = ?");
                    deleteOldCode.setString(1, String.valueOf(code));
                    deleteOldCode.execute();
                } else {
                    return false;
                }
            }

            //Remove all old codes
            PreparedStatement deletePreviousLinks = connection.prepareStatement("DELETE FROM linking WHERE uuid = ?");
            deletePreviousLinks.setString(1, uuid.toString());
            deletePreviousLinks.execute();

            //Insert the code
            PreparedStatement insertCodeStatement = connection.prepareStatement("INSERT INTO linking (uuid,code,created_at) VALUES (?,?,?)");
            insertCodeStatement.setString(1, uuid.toString());
            insertCodeStatement.setString(2, String.valueOf(code));
            insertCodeStatement.setString(3, String.valueOf(currentTime));
            insertCodeStatement.execute();
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check a code and return a UserInfo object.
     *
     * @param code
     * @return
     */
    public UUID checkCode(int code) {
        try {
            //Prepare an SQL Statement and then execute it
            PreparedStatement checkCodeStatement = connection.prepareStatement("SELECT * FROM linking WHERE code = ?");
            checkCodeStatement.setString(1, String.valueOf(code));
            ResultSet resultSet = checkCodeStatement.executeQuery();

            //Check results aren't empty
            while (resultSet.next()) {
                //Remove old codes
                PreparedStatement deletePreviousLinks = connection.prepareStatement("DELETE FROM linking WHERE code = ?");
                deletePreviousLinks.setString(1, String.valueOf(code));
                deletePreviousLinks.execute();

                return UUID.fromString(resultSet.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Close the connection
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AllArgsConstructor
    @Getter
    public class UserInfo {
        private UUID uuid;
        private String discordSnowflake;
    }
}
