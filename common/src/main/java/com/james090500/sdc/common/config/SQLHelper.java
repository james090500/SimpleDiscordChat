package com.james090500.sdc.common.config;

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
                System.out.printf("SQL Failed, this is a fatal error!");
                return;
            }

            String createUsersTable = "CREATE TABLE IF not EXISTS users (\n" +
                "\tid integer PRIMARY KEY,\n" +
                "uuid VARCHAR(36) NOT NULL,\n" +
                "discord_snowflake VARCHAR(36) NULL\n" +
            ")\n";

            //Statement
            Statement statement = connection.createStatement();
            statement.execute(createUsersTable);

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
                UserInfo userInfo = new UserInfo(uuid, resultSet.getString(2));
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
        try {
            //Check if we have a cache
            UserInfo cachedPlayer = getPlayer(uuid);
            PreparedStatement preparedStatement;

            //Insert or update
            if (cachedPlayer != null) {
                preparedStatement = connection.prepareStatement("UPDATE users SET discord_snowflake = ? WHERE uuid = ?");
                preparedStatement.setString(1, discordSnowflake);
                preparedStatement.setString(2, uuid.toString());
            } else {
                preparedStatement = connection.prepareStatement("INSERT INTO users (uuid, discord_snowflake) VALUES(?,?)");
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, discordSnowflake);
            }

            UserInfo userInfo = new UserInfo(uuid, discordSnowflake);
            userCache.put(uuid, userInfo);

            preparedStatement.executeUpdate();
        } catch(SQLException e) {
            e.printStackTrace();
        }
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
