package com.james090500.sdc.velocity.helpers;
import com.velocitypowered.api.proxy.Player;
import org.mineacademy.velocitycontrol.SyncedCache;

public class ChatControlHelper {

    /**
     * Tries to get the nickname for a player from ChatControl
     * @param player The player in question
     * @return
     */
    public static String getNick(Player player) {
        if(!doesClassExist("org.mineacademy.velocitycontrol.SyncedCache")) {
            return null;
        }

        String nickname = SyncedCache.fromName(player.getUsername()).getNick();
        return nickname != null ? nickname : player.getUsername();
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