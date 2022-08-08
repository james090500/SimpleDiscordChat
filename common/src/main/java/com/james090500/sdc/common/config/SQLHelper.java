package com.james090500.sdc.common.config;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.api.SDCPlayer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLHelper {


    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.setPoolName("SimpleDiscordChat");
        config.setConnectionTestQuery("SELECT 1");
        config.setLeakDetectionThreshold(10000);

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

        try {
            String createUsersTable = "CREATE TABLE IF not EXISTS users (id integer PRIMARY KEY,uuid VARCHAR(36) NOT NULL,discord_snowflake VARCHAR(36) NULL)";
            String createLinkingTable = "CREATE TABLE IF not EXISTS linking (id integer PRIMARY KEY,uuid VARCHAR(36) NOT NULL,code VARCHAR(4) UNIQUE, created_at VARCHAR(10) NOT NULL)";

            Connection connection = ds.getConnection();

            PreparedStatement createUsers = connection.prepareStatement(createUsersTable);
            PreparedStatement createLinking = connection.prepareStatement(createLinkingTable);

            createUsers.execute();
            createLinking.execute();

            createUsers.close();
            createLinking.close();

            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get players info from the database
     * @param sdcPlayer The plaeyr
     * @return The players info
     */
    public static SDCPlayer getPlayer(SDCPlayer sdcPlayer) {
        try {
            Connection connection = ds.getConnection();

            //Prepare an SQL Statement and then execute it
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE uuid = ?");
            preparedStatement.setString(1, sdcPlayer.getUuid().toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            //Check results aren't empty
            while(resultSet.next()) {
                //Create a new user info instance and return the data
                sdcPlayer.setDiscordSnowflake(resultSet.getString(3));
                resultSet.close();
                preparedStatement.close();
                connection.close();
                return sdcPlayer;
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Will update the player in the database
     * @param sdcPlayer Players to update
     */
    public static void updatePlayer(SDCPlayer sdcPlayer) {
        try {
            Connection connection = ds.getConnection();

            //Remove duplicates
            PreparedStatement deleteOldPlayer = connection.prepareStatement("DELETE FROM users WHERE uuid = ?");
            deleteOldPlayer.setString(1, sdcPlayer.getUuid().toString());
            deleteOldPlayer.execute();
            deleteOldPlayer.close();

            if(sdcPlayer.getDiscordSnowflake() != null) {
                PreparedStatement deleteOldDiscord = connection.prepareStatement("DELETE FROM users WHERE discord_snowflake = ?");
                deleteOldDiscord.setString(1, sdcPlayer.getDiscordSnowflake());
                deleteOldDiscord.execute();
                deleteOldDiscord.close();
            }

            //Insert
            PreparedStatement insertPlayer = connection.prepareStatement("INSERT INTO users (uuid, discord_snowflake) VALUES(?,?)");
            insertPlayer.setString(1, sdcPlayer.getUuid().toString());
            insertPlayer.setString(2, sdcPlayer.getDiscordSnowflake());
            insertPlayer.executeUpdate();
            insertPlayer.close();

            connection.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Insert a new linking code for a user and remove all old ones
     * @param uuid The users uuid
     * @param code The code to insert
     * @return
     */
    public static boolean updateLinking(UUID uuid, int code) {
        long currentTime = System.currentTimeMillis() / 1000;
        try {
            Connection connection = ds.getConnection();

            //Prepare an SQL Statement and then execute it
            PreparedStatement checkCodeStatement = connection.prepareStatement("SELECT * FROM linking WHERE code = ?");
            checkCodeStatement.setString(1, String.valueOf(code));
            ResultSet resultSet = checkCodeStatement.executeQuery();

            //Check if the results are empty and if they need a new code
            while(resultSet.next()) {
                long createdAt = Long.parseLong(resultSet.getString(3));
                checkCodeStatement.close();
                resultSet.close();
                if((currentTime - createdAt) > 300) {
                    PreparedStatement deleteOldCode = connection.prepareStatement("DELETE FROM linking WHERE code = ?");
                    deleteOldCode.setString(1, String.valueOf(code));
                    deleteOldCode.execute();
                    deleteOldCode.close();
                } else {
                    connection.close();
                    return false;
                }
            }
            checkCodeStatement.close();
            resultSet.close();

            //Remove all old codes
            PreparedStatement deletePreviousLinks = connection.prepareStatement("DELETE FROM linking WHERE uuid = ?");
            deletePreviousLinks.setString(1, uuid.toString());
            deletePreviousLinks.execute();
            deletePreviousLinks.close();

            //Insert the code
            PreparedStatement insertCodeStatement = connection.prepareStatement("INSERT INTO linking (uuid,code,created_at) VALUES (?,?,?)");
            insertCodeStatement.setString(1, uuid.toString());
            insertCodeStatement.setString(2, String.valueOf(code));
            insertCodeStatement.setString(3, String.valueOf(currentTime));
            insertCodeStatement.execute();
            insertCodeStatement.close();

            connection.close();
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
            Connection connection = ds.getConnection();

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
                deletePreviousLinks.close();

                UUID uuidResult = UUID.fromString(resultSet.getString(2));

                checkCodeStatement.close();
                resultSet.close();
                connection.close();
                return uuidResult;
            }
            checkCodeStatement.close();
            resultSet.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Close the connection
     */
    public static void close() {
        ds.close();
    }
}
