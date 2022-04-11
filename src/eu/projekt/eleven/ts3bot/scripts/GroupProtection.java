package eu.projekt.eleven.ts3bot.scripts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import eu.projekt.eleven.ts3bot.Main;
import eu.projekt.eleven.ts3bot.helper.ConfigManager;
import eu.projekt.eleven.ts3bot.helper.Helper;
import eu.projekt.eleven.ts3bot.helper.TS3ApiEnhanced;
import org.json.simple.JSONArray;

import java.util.List;
import java.util.Map;

public class GroupProtection
{
    private static final TS3Api ts3Api = Main.getTs3Api();
    private static final ConfigManager configManager = new ConfigManager("groupProtection");

    public static void run()
    {
        JSONArray protectedGroups = configManager.getValue("protectedGroups", JSONArray.class);

        for(Map.Entry<String, List<Integer>> entry : TS3ApiEnhanced.getAllGroupChanges().entrySet())
        {
            for(Integer groupId : entry.getValue())
            {
                System.out.println(Helper.jsonArrayContainsInteger(protectedGroups, groupId));

                // check if group is protected
                if(Helper.jsonArrayContainsInteger(protectedGroups, groupId))
                {
                    // check if user is in the given server group
                    System.out.println(entry.getKey() + ":::" + ts3Api.isClientOnline(entry.getKey()));

                    if(ts3Api.isClientOnline(entry.getKey()))
                    {
                        Client client = ts3Api.getClientByUId(entry.getKey());
                        System.out.println(entry.getKey() + ":::" + client);

                        if(client != null)
                        {
                            // remove from server group
                            if(client.isInServerGroup(groupId))
                                ts3Api.removeClientFromServerGroup(groupId, client.getDatabaseId());
                            // add to server group
                            else
                                ts3Api.addClientToServerGroup(groupId, client.getDatabaseId());
                        }
                    }
                }
            }
        }
    }
}