package eu.projekt.eleven.ts3bot.scripts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import eu.projekt.eleven.ts3bot.Main;
import eu.projekt.eleven.ts3bot.helper.ConfigManager;
import eu.projekt.eleven.ts3bot.helper.Helper;
import eu.projekt.eleven.ts3bot.helper.TS3ApiEnhanced;
import org.json.simple.JSONArray;

public class NotPokeableSetter
{
    private static final TS3Api ts3Api = Main.getTs3Api();
    private static final ConfigManager configManager = new ConfigManager("notPokeableSetter");

    public static void registerEvents()
    {
        ts3Api.registerEvent(TS3EventType.CHANNEL);
        ts3Api.addTS3Listeners(new TS3EventAdapter()
        {
            @Override
            public void onClientMoved(ClientMovedEvent event)
            {
                Client client = TS3ApiEnhanced.getOnlineClientById(event.getClientId());

                if(client == null)
                    return;

                // add client to the group that should set if he is in a channel of the list, and he has not already the specific group
                if(Helper.jsonArrayContainsInteger(configManager.getValue("channelsInWhichTheGroupIsSet", JSONArray.class), event.getTargetChannelId()) &&
                        !TS3ApiEnhanced.hasGroup(client, configManager.getValue("groupThatShouldSet", Integer.class)))
                    ts3Api.addClientToServerGroup(configManager.getValue("groupThatShouldSet", Integer.class), client.getDatabaseId());
                // remove client from group if he is not anymore a member of a channel that is on the list
                else if(!Helper.jsonArrayContainsInteger(configManager.getValue("channelsInWhichTheGroupIsSet", JSONArray.class), event.getTargetChannelId()) &&
                        TS3ApiEnhanced.hasGroup(client, configManager.getValue("groupThatShouldSet", Integer.class)))
                    ts3Api.removeClientFromServerGroup(configManager.getValue("groupThatShouldSet", Integer.class), client.getDatabaseId());
            }
        });
    }
}