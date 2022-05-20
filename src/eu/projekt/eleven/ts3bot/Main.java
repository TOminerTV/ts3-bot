package eu.projekt.eleven.ts3bot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import eu.projekt.eleven.ts3bot.helper.UnixSystemColors;
import eu.projekt.eleven.ts3bot.scripts.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Main
{
    private static final TS3Config ts3Config = new TS3Config();
    private static TS3Api ts3Api;

    public static void main(String[] args)
    {
        try
        {
            JSONParser jsonParser = new JSONParser();
            JSONObject botData = (JSONObject) jsonParser.parse(new FileReader(getBotPath() + "/bot.json"));

            // set host (server)
            ts3Config.setHost(botData.get("host").toString());
            ts3Config.setEnableCommunicationsLogging(true);
            ts3Config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
            ts3Config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());

            TS3Query ts3Query = new TS3Query(ts3Config);
            ts3Query.connect();
            if(ts3Query.isConnected())
            {
                System.out.println(UnixSystemColors.GREEN + "Successfully connected to: " + botData.get("host").toString());

                ts3Api = ts3Query.getApi();
                ts3Api.login(botData.get("login").toString(), botData.get("password").toString());
                ts3Api.selectVirtualServerById(1);
                ts3Api.moveClient(ts3Api.whoAmI().getId(), Integer.parseInt(botData.get("channel").toString()));

                if(!ts3Api.whoAmI().getNickname().equals(botData.get("name").toString()))
                    ts3Api.setNickname(botData.get("name").toString());
            }
            else
            {
                System.out.println(UnixSystemColors.RED + "Bot cant connected to: " + botData.get("host").toString());
            }

            // create data folder
            File dataFolder = new File(getBotPath() + "/data");
            if(!dataFolder.exists())
                dataFolder.mkdirs();

// TODO::            ts3Query.exit();

            // start scripts
//            AFKKicker.run();

            // restore script data if a script saves it
            AFKMover.restore();
            GreenWingSetter.restore();

            // register script events
            SupportNotifier.registerEvents();
            NotPokeableSetter.registerEvents();

            // loop through all normal scripts
            new Timer().scheduleAtFixedRate(new TimerTask()
            {
                // run scripts every 5 seconds
                @Override
                public void run()
                {
                    try
                    {
                        AFKMover.run();
                        SupportNotifier.run();
                        GreenWingSetter.run();
                        TS3DataSaver.run();
                    }
                    catch(Exception exception)
                    {
                        System.err.println(UnixSystemColors.RED + "BOT :: Bot Error: " + exception.getMessage());
                    }
                }
            },0,5000);

            // loop through all security scripts
//            new Timer().scheduleAtFixedRate(new TimerTask()
//            {
//                // run scripts every 0.5 seconds
//                @Override
//                public void run()
//                {
//                    try
//                    {
//                        GroupProtection.run();
//                    }
//                    catch(Exception exception)
//                    {
//                        System.err.println(UnixSystemColors.RED + "BOT :: Bot Error: " + exception.getMessage());
//                    }
//                }
//            },0,500);
        }
        catch(IOException | ParseException exception)
        {
            exception.printStackTrace();
        }
    }

    public static String getBotPath()
    {
        return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    }

    public static TS3Api getTs3Api()
    {
        return ts3Api;
    }
}