package com.james090500.sdc.common.handlers;

import com.james090500.sdc.common.SimpleDiscordChat;
import com.james090500.sdc.common.config.SQLHelper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SyncHandler {

    public void doSync(UUID uuid, String username, String groupName) {
        new Thread(() -> {
            //Initialise all needed variables
            String discordUser;
            String discordGroup = SimpleDiscordChat.getInstance().getConfigs().getSettingsConfig().getSyncing().getGroups().get(groupName);

            //Get SQL data
            SQLHelper.UserInfo userInfo = SimpleDiscordChat.getInstance().getSqlHelper().getPlayer(uuid);
            if(userInfo == null) return;
            discordUser = userInfo.getDiscordSnowflake();

            //Remove all roles from the user that we sync
            Guild currentGuild = SimpleDiscordChat.getInstance().getChatChannel().getGuild();

            //All server roles
            List<Role> allRoles = new ArrayList<>();
            List<Role> rolesToAdd = new ArrayList<>();

            //Populate roles
            SimpleDiscordChat.getInstance().getConfigs().getSettingsConfig().getSyncing().getGroups().forEach((snowflake, name) -> allRoles.add(currentGuild.getRoleById(snowflake)));

            //Get the member and remove the new role from removal list
            Member member = currentGuild.getMemberById(discordUser);
            Role newRole = currentGuild.getRoleById(discordGroup);
            allRoles.remove(newRole);

            //Update the user
            if(member != null) {
                currentGuild.modifyMemberRoles(member, rolesToAdd, allRoles).queue();
                currentGuild.modifyNickname(member, username).queue();
            }
        }).start();
    }

}
