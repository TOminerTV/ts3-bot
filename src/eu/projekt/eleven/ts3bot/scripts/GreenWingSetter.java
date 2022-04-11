package eu.projekt.eleven.ts3bot.scripts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import eu.projekt.eleven.ts3bot.Main;
import eu.projekt.eleven.ts3bot.helper.ConfigManager;
import eu.projekt.eleven.ts3bot.helper.Helper;
import eu.projekt.eleven.ts3bot.helper.TS3ApiEnhanced;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public final class GreenWingSetter
{
    private static final String configDataName = "greenWingSetter";

    private static final TS3Api ts3Api = Main.getTs3Api();
    private static final ConfigManager configManager = new ConfigManager(configDataName);
    private static final JSONParser jsonParser = new JSONParser();

    private static final HashMap<String, Long> onlineTime = new HashMap<>();
    private static final Properties properties = new Properties();

    public static void restore()
    {
        // load saved online time from data folder file
        restoreSavedScriptData();
    }

    public static void run()
    {
        for(Client client : ts3Api.getClients())
        {
            // check if client is in ignored channel
            if(!Helper.jsonArrayContainsInteger(configManager.getValue("channelsThatWillBeIgnored", JSONArray.class), client.getChannelId()))
            {
                // check if client is already on the list => then update the online time
                if(onlineTime.containsKey(client.getUniqueIdentifier()))
                {
                    if(TS3ApiEnhanced.hasGroup(client, configManager.getValue("groupThatShouldCounted", Integer.class)))
                    {
                        onlineTime.put(client.getUniqueIdentifier(), onlineTime.get(client.getUniqueIdentifier()) + 5L);
                    }
                }
                // add the client to the online time list if he has the right server group, and he has not the group to set
                else if(TS3ApiEnhanced.hasGroup(client, configManager.getValue("groupThatShouldCounted", Integer.class)) && !TS3ApiEnhanced.hasGroup(client, configManager.getValue("greenWingGroup", Integer.class)))
                {
                    onlineTime.put(client.getUniqueIdentifier(), 0L);
                }
            }

            // check if the client has been online long enough for the green wing, and he has it not already
            if(!TS3ApiEnhanced.hasGroup(client, configManager.getValue("greenWingGroup", Integer.class)) && onlineTime.containsKey(client.getUniqueIdentifier()) && onlineTime.get(client.getUniqueIdentifier()) >= configManager.getValue("waitInSecondsBeforeSet", Integer.class))
            {
                // set the green wing group and remove the user from the hashmap
                ts3Api.addClientToServerGroup(configManager.getValue("greenWingGroup", Integer.class), client.getDatabaseId());
                onlineTime.remove(client.getUniqueIdentifier());
            }
        }

        // save afkUsers hashmap into the data folder
        saveScriptData();
    }

    // TODO:: update
    @Deprecated
    private static void saveScriptData()
    {
        properties.clear();

        try
        {
            for(Map.Entry<String, Long> entry : onlineTime.entrySet())
            {
                properties.put(entry.getKey(), entry.getValue().toString());
            }

            properties.store(new FileOutputStream(Main.getBotPath() + "/data/" + configDataName + ".save"), null);
        }
        catch(IOException exception)
        {
            exception.printStackTrace();
        }
    }

    @Deprecated
    private static void restoreSavedScriptData()
    {
        try
        {
            if(!new File(Main.getBotPath() + "/data/" + configDataName + ".save").exists())
                return;

            properties.load(new FileInputStream(Main.getBotPath() + "/data/" + configDataName + ".save"));

            for(String key : properties.stringPropertyNames())
            {
                onlineTime.put(key, (Long) jsonParser.parse(properties.get(key).toString()));
            }
        }
        catch(IOException | ParseException exception)
        {
            exception.printStackTrace();
        }
    }
}