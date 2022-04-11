package eu.projekt.eleven.ts3bot.scripts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import eu.projekt.eleven.ts3bot.Main;
import eu.projekt.eleven.ts3bot.helper.ConfigManager;
import eu.projekt.eleven.ts3bot.helper.TS3ApiEnhanced;
import org.json.simple.JSONArray;

public final class SupportNotifier
{
    private static final TS3Api ts3Api = Main.getTs3Api();
    private static final ConfigManager configManager = new ConfigManager("supportNotifier");

    public static void run()
    {
        int supporterAmount = 0;
        for(Client client : ts3Api.getClients())
        {
            for(Object supportGroupId : configManager.getValue("supportGroupIds", JSONArray.class))
            {
                if(client.isInServerGroup(Integer.parseInt(supportGroupId.toString())))
                {
                    supporterAmount++;
                    break;
                }
            }
        }

        String newChannelName = configManager.getValue("supportWaitingChannelName", String.class).replaceAll("%SUPPORTER_AMOUNT%", String.valueOf(supporterAmount));

        if(!ts3Api.getChannelInfo(configManager.getValue("supportWaitingChannel", Integer.class)).getName().equals(newChannelName))
            ts3Api.editChannel(configManager.getValue("supportWaitingChannel", Integer.class), ChannelProperty.CHANNEL_NAME, newChannelName);
    }

    public static void registerEvents()
    {
        ts3Api.registerEvent(TS3EventType.CHANNEL);
        ts3Api.addTS3Listeners(new TS3EventAdapter()
        {
            @Override
            public void onClientMoved(ClientMovedEvent event)
            {
                if(event.getTargetChannelId() == configManager.getValue("supportWaitingChannel", Integer.class))
                    TS3ApiEnhanced.pokeClients(configManager.getValue("supportGroupIds", JSONArray.class), configManager.getValue("clientNeedsSupportMessage", String.class).replaceAll("%USER_NAME%", ts3Api.getClientInfo(event.getClientId()).getNickname()));
            }
        });
    }
}