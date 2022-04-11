package eu.projekt.eleven.ts3bot.scripts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import eu.projekt.eleven.ts3bot.Main;
import eu.projekt.eleven.ts3bot.helper.ConfigManager;
import eu.projekt.eleven.ts3bot.helper.Helper;
import eu.projekt.eleven.ts3bot.helper.TS3ApiEnhanced;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public final class AFKMover
{
    private static final HashMap<String, JSONObject> afkClients = new HashMap<>();
    private static final Properties properties = new Properties();

    private static final TS3Api ts3Api = Main.getTs3Api();
    private static final ConfigManager configManager = new ConfigManager("afkMover");
    private static final JSONParser jsonParser = new JSONParser();

    public static void restore()
    {
        // load saved afkUsers from data folder file
        restoreSavedScriptData();
    }

    public static void run()
    {
        int afkChannel = configManager.getValue("afkChannel", Integer.class);

        for(Client client : ts3Api.getClients())
        {
            if(TS3ApiEnhanced.isBot(client))
                continue;

            if(TS3ApiEnhanced.isAFK(client, configManager.getValue("maxIdleTimeInSeconds", Integer.class)) &&
                    !Helper.jsonArrayContainsInteger(configManager.getValue("channelThatWillBeIgnored", JSONArray.class), client.getChannelId()) &&
                    client.getChannelId() != afkChannel)
            {
                JSONObject afkClientJSONObject;

                // update users old channel
                if(afkClients.containsKey(client.getUniqueIdentifier()))
                {
                    afkClientJSONObject = afkClients.get(client.getUniqueIdentifier());
                    afkClientJSONObject.put("oldChannel", client.getChannelId());
                }
                // add user to the afk list
                else
                {
                    afkClientJSONObject = new JSONObject();
                    afkClientJSONObject.put("oldChannel", client.getChannelId());
                    afkClientJSONObject.put("timestamp", new Date().getTime());
                }

                afkClients.put(client.getUniqueIdentifier(), afkClientJSONObject);
            }
            else if(afkClients.containsKey(client.getUniqueIdentifier()) && !TS3ApiEnhanced.isAFK(client, configManager.getValue("maxIdleTimeInSeconds", Integer.class)))
            {
                int oldChannel = Integer.parseInt(afkClients.get(client.getUniqueIdentifier()).get("oldChannel").toString());

                // move only back if the client is not longer afk and the client is in the afk channel
                if(client.getChannelId() == afkChannel && oldChannel != afkChannel)// && client.getChannelId() != oldChannel)
                    ts3Api.moveClient(client.getId(), oldChannel);

                afkClients.remove(client.getUniqueIdentifier());
            }
        }

        List<String> clientsToBeRemoved = new ArrayList<>();

        for(Map.Entry<String, JSONObject> afkClient : afkClients.entrySet())
        {
            Client client = ts3Api.isClientOnline(afkClient.getKey()) ? ts3Api.getClientByUId(afkClient.getKey()) : null;

            if(client == null)
            {
                clientsToBeRemoved.add(afkClient.getKey());
                continue;
            }

            if(client.getChannelId() == afkChannel)
                continue;

            if(Long.parseLong(afkClient.getValue().get("timestamp").toString()) <= (new Date().getTime() - (configManager.getValue("afkWaitInSeconds", Long.class)) * 1000))
                ts3Api.moveClient(client.getId(), afkChannel);
        }

        // remove all clients that left the server
        clientsToBeRemoved.forEach(afkClients.keySet()::remove);

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
            for(Map.Entry<String, JSONObject> entry : afkClients.entrySet())
            {
                if(ts3Api.isClientOnline(entry.getKey()) && ts3Api.getClientByUId(entry.getKey()).getChannelId() == configManager.getValue("afkChannel", Integer.class))
                    properties.put(entry.getKey(), entry.getValue().toJSONString());
            }

            properties.store(new FileOutputStream(Main.getBotPath() + "/data/afkMover.save"), null);
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
            if(!new File(Main.getBotPath() + "/data/afkMover.save").exists())
                return;

            properties.load(new FileInputStream(Main.getBotPath() + "/data/afkMover.save"));

            for(String key : properties.stringPropertyNames())
            {
                afkClients.put(key, (JSONObject) jsonParser.parse(properties.get(key).toString()));
            }
        }
        catch(IOException | ParseException exception)
        {
            exception.printStackTrace();
        }
    }
}