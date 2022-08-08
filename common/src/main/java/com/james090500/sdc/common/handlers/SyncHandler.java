package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.Configs;
import com.james090500.sdc.common.config.SQLHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncHandler {

    private static Guild currentGuild = SimpleDiscordChat.getInstance().getChatChannel().getGuild();

    /**
     * Start checks for syncing username and groups
     * @param uuid The Minecraft user to sync
     * @param username The username to sync
     * @param newGroupName The group to sync
     */
    public static void doSync(UUID uuid, String username, String newGroupName) {
        new Thread(() -> {
            //Check if syncing is enabled
            if (Configs.getSettingsConfig().isSyncGroups() || Configs.getSettingsConfig().isSyncUsernames()) {
                //Get SQL data
                SQLHelper.UserInfo userInfo = SQLHelper.getPlayer(uuid);
                if (userInfo == null) return;

                //Get the member and check if it can be edited
                Member member = currentGuild.getMemberById(userInfo.getDiscordSnowflake());
                if (member == null) return;
                if (!currentGuild.getMember(SimpleDiscordChat.getInstance().getBot()).canInteract(member)) return;

                //Sync Groups
                if (Configs.getSettingsConfig().isSyncGroups()) {
                    doSyncGroups(member, uuid, newGroupName);
                }

                //Sync usernames
                if (Configs.getSettingsConfig().isSyncUsernames()) {
                    doSyncUsername(member, username);
                }
            }
        }).start();
    }

    /**
     * Sync a users groups
     * @param member The Discord member to sync
     * @param uuid The Minecraft user to sync
     * @param newGroupName The group to sync
     */
    private static void doSyncGroups(Member member, UUID uuid, String newGroupName) {
        String discordGroup = Configs.getSettingsConfig().getSyncing().getGroups().get(newGroupName);

        //All server roles
        List<Role> rolesToAdd = new ArrayList<>();
        List<Role> rolesToRemove = new ArrayList<>();

        //Populate all roles (for removal)
        Role boostRole = currentGuild.getBoostRole();
        Configs.getSettingsConfig().getSyncing().getGroups().forEach((groupName, snowflake) -> {
            if(boostRole.getId().equals(snowflake)) {
                if(member.getRoles().contains(boostRole)) {
                    SimpleDiscordChat.getInstance().getServerInterface().addBoostRank(uuid, groupName);
                }
            } else {
                rolesToRemove.add(currentGuild.getRoleById(snowflake));
            }
        });

        //Get the roles to add and remove it from the roleToRemove
        Role newRole = currentGuild.getRoleById(discordGroup);
        rolesToAdd.add(newRole);
        rolesToRemove.remove(newRole);

        //Update the user
        if(!member.getRoles().containsAll(rolesToAdd) || member.getRoles().containsAll(rolesToRemove)) {
            currentGuild.modifyMemberRoles(member, rolesToAdd, rolesToRemove).queue();
        }
    }

    /**
     * Sync a users username
     * @param member The Discord member to sync
     * @param username The username to sync
     */
    private static void doSyncUsername(Member member, String username) {
        if(!member.getNickname().equals(username)) {
            currentGuild.modifyNickname(member, username).queue();
        }
    }

}
