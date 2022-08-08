package com.james090500.sdc.common.config;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class SQLHelper {


    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    private static HashMap<UUID, UserInfo> userCache = new HashMap<>();

    static {
        config.setPoolName("SimpleDiscordChat");
        config.setConnectionTestQuery("SELECT 1");

        if(Configs.getSettingsConfig().getJdbc().isEnabled()) {
            config.setUsername(Configs.getSettingsConfig().getJdbc().getUsername());
            config.setPassword(Configs.getSettingsConfig().getJdbc().getPassword());
            config.addDataSourceProperty( "cachePrepStmts" , "true" );
            config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
            config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        } else {
            config.setDriverClassName("org.sqlite.JDBC");
            config.setJdbcUrl("jdbc:sqlite:" + SimpleDiscordChat.getInstance().getDataFolder() + "/users.db");
            config.setMaxLifetime(60000);
            config.setIdleTimeout(45000);
            config.setMaximumPoolSize(50);
        }

        ds = new HikariDataSource(config);
    }

    /**
     * Get the SQL connection
     * @return
     */
    private static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    /**
     * Get players info from the database
     * @param uuid Player uuid
     * @return The players info
     */
    public static UserInfo getPlayer(UUID uuid) {
        //Return from the cache!
        UserInfo cachedPlayer = userCache.get(uuid);
        if(cachedPlayer != null) return cachedPlayer;

        try {
            //Prepare an SQL Statement and then execute it
            PreparedStatement preparedStatement = getConnection().prepareStatement("SELECT * FROM users WHERE uuid = ?");
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
    public static void updatePlayer(UUID uuid, @Nullable String discordSnowflake) {
        forgetPlayer(uuid);
        try {
            //Remove duplicates
            PreparedStatement deleteOldPlayer = getConnection().prepareStatement("DELETE FROM users WHERE uuid = ?");
            deleteOldPlayer.setString(1, uuid.toString());
            deleteOldPlayer.execute();
            PreparedStatement deleteOldDiscord = getConnection().prepareStatement("DELETE FROM users WHERE discord_snowflake = ?");
            deleteOldDiscord.setString(1, discordSnowflake);
            deleteOldDiscord.execute();

            //Insert
            PreparedStatement preparedStatement = getConnection().prepareStatement("INSERT INTO users (uuid, discord_snowflake) VALUES(?,?)");
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
    public static void forgetPlayer(UUID uuid) {
        userCache.remove(uuid);
    }

    /**
     * Insert a new linking code for a user and remove all old ones
     * @param uuid The users uuid
     * @param code The code to insert
     * @return
     */
    public static boolean updateLinking(UUID uuid, int code) {
        forgetPlayer(uuid);
        long currentTime = System.currentTimeMillis() / 1000;
        try {
            //Prepare an SQL Statement and then execute it
            PreparedStatement checkCodeStatement = getConnection().prepareStatement("SELECT * FROM linking WHERE code = ?");
            checkCodeStatement.setString(1, String.valueOf(code));
            ResultSet resultSet = checkCodeStatement.executeQuery();

            //Check if the results are empty and if they need a new code
            while(resultSet.next()) {
                long createdAt = Long.parseLong(resultSet.getString(3));
                if((currentTime - createdAt) > 300) {
                    PreparedStatement deleteOldCode = getConnection().prepareStatement("DELETE FROM linking WHERE code = ?");
                    deleteOldCode.setString(1, String.valueOf(code));
                    deleteOldCode.execute();
                } else {
                    return false;
                }
            }

            //Remove all old codes
            PreparedStatement deletePreviousLinks = getConnection().prepareStatement("DELETE FROM linking WHERE uuid = ?");
            deletePreviousLinks.setString(1, uuid.toString());
            deletePreviousLinks.execute();

            //Insert the code
            PreparedStatement insertCodeStatement = getConnection().prepareStatement("INSERT INTO linking (uuid,code,created_at) VALUES (?,?,?)");
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
    public static UUID checkCode(int code) {
        try {
            //Prepare an SQL Statement and then execute it
            PreparedStatement checkCodeStatement = getConnection().prepareStatement("SELECT * FROM linking WHERE code = ?");
            checkCodeStatement.setString(1, String.valueOf(code));
            ResultSet resultSet = checkCodeStatement.executeQuery();

            //Check results aren't empty
            while (resultSet.next()) {
                //Remove old codes
                PreparedStatement deletePreviousLinks = getConnection().prepareStatement("DELETE FROM linking WHERE code = ?");
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
    public static void close() {
        try {
            getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AllArgsConstructor
    @Getter
    public static class UserInfo {
        private UUID uuid;
        private String discordSnowflake;
    }
}
