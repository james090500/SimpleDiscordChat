package com.james090500.sdc.velocity.helpers;

import org.mineacademy.velocitycontrol.SyncedCache;

public class ChatControlHelper {

    /**
     * Tries to get the nickname for a player from ChatControl
     * @param username The player in question
     * @return
     */
    public static String getNick(String username) {
        if(!doesClassExist("org.mineacademy.velocitycontrol.SyncedCache")) {
            return username;
        }

        SyncedCache syncedCache = SyncedCache.fromName(username);
        return syncedCache != null ? syncedCache.getNick() : username;
    }

    /**
     * Checks if a class exists or not
     * @param name
     * @return
     */
    private static boolean doesClassExist(String name) {
        try {
            Class c = Class.forName(name);
            if (c != null) {
                return true;
            }
        } catch (ClassNotFoundException e) {}
        return false;
    }

}