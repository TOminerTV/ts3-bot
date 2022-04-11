package eu.projekt.eleven.ts3bot.scripts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import eu.projekt.eleven.ts3bot.Main;
import eu.projekt.eleven.ts3bot.helper.UnixSystemColors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public final class AFKKicker
{
    private static final TS3Api ts3Api = Main.getTs3Api();
    private static final JSONParser jsonParser = new JSONParser();

    // TODO: not saved on bot quit / crash
    public static void run()
    {
        try
        {
            new Timer().scheduleAtFixedRate(new TimerTask()
            {
                @Override
                public void run()
                {
                    try
                    {
                        JSONObject dataJSONObject = (JSONObject) jsonParser.parse(new FileReader(Main.getBotPath() + "/configs/afkKicker.json"));

                        long timeToWaitBeforeKick = Long.parseLong(((JSONObject) dataJSONObject.get("timeToWaitBeforeKick")).get("value").toString());
                        JSONArray channelIgnoreArray = ((JSONArray)((JSONObject) dataJSONObject.get("channelThatWillBeIgnored")).get("value"));
                        String kickMessage = ((JSONObject) dataJSONObject.get("kickMessage")).get("value").toString();

                        List<Client> ts3Clients = ts3Api.getClients();
                        for(int i=0; i < ts3Clients.size(); i++)
                        {
                            // kick client if he is longer afk (idle) as the time given from the config file
                            if(ts3Clients.get(i).getIdleTime() >= timeToWaitBeforeKick * 1000 && !channelIgnoreArray.contains(ts3Clients.get(i).getChannelId()) && ts3Api.isClientOnline(ts3Clients.get(i).getId()))
                                ts3Api.kickClientFromServer(kickMessage, ts3Clients.get(i));
                        }
                    }
                    catch(IOException | ParseException exception)
                    {
                        System.err.println(UnixSystemColors.RED + "[AFK-KICKER] Script Error: " + exception.getMessage());
                    }
                }
            },0,5000);
        }
        catch(Exception exception)
        {
            System.err.println(UnixSystemColors.RED + "[AFK-KICKER] Script Error: " + exception.getMessage());
        }
    }
}