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

    /**
     * Runs the sync of groups, username etc
     * @param uuid The uuid to sync
     * @param username Their username/display name
     * @param groupName Their primary group name
     */
    public static void doSync(UUID uuid, String username, String groupName) {
        new Thread(() -> {
            //Initialise all needed variables
            String discordUser;
            String discordGroup = Configs.getSettingsConfig().getSyncing().getGroups().get(groupName);

            //Get SQL data
            SQLHelper.UserInfo userInfo = SQLHelper.getPlayer(uuid);
            if(userInfo == null) return;
            discordUser = userInfo.getDiscordSnowflake();

            //Remove all roles from the user that we sync
            Guild currentGuild = SimpleDiscordChat.getInstance().getChatChannel().getGuild();

            //Get the member and check if it can be edited
            Member member = currentGuild.getMemberById(discordUser);
            if(member == null) return;
            if(!currentGuild.getMember(SimpleDiscordChat.getInstance().getBot()).canInteract(member)) return;

            //All server roles
            List<Role> rolesToAdd = new ArrayList<>();
            List<Role> rolesToRemove = new ArrayList<>();

            //Populate all roles (for removal)
            Configs.getSettingsConfig().getSyncing().getGroups().forEach((name, snowflake) -> rolesToRemove.add(currentGuild.getRoleById(snowflake)));

            //Get the roles to add and remove it from the roleToRemove
            Role newRole = currentGuild.getRoleById(discordGroup);
            rolesToAdd.add(newRole);
            rolesToRemove.remove(newRole);

            //Update the user
            if(!member.getRoles().containsAll(rolesToAdd) || member.getRoles().containsAll(rolesToRemove) || !member.getNickname().equals(username)) {
                SimpleDiscordChat.getInstance().getLogger().error("Updating member");
                currentGuild.modifyMemberRoles(member, rolesToAdd, rolesToRemove).queue();
                currentGuild.modifyNickname(member, username).queue();
            }
        }).start();
    }

}
