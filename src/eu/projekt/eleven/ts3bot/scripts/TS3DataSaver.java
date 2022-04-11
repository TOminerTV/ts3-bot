package eu.projekt.eleven.ts3bot.scripts;

import com.github.theholywaffle.teamspeak3.TS3Api;
import eu.projekt.eleven.ts3bot.Main;
import eu.projekt.eleven.ts3bot.helper.Helper;
import eu.projekt.eleven.ts3bot.helper.TS3ApiEnhanced;
import org.json.simple.JSONObject;

public class TS3DataSaver
{
    private static final TS3Api ts3Api = Main.getTs3Api();

    public static void run()
    {
        JSONObject ts3Data = new JSONObject();

        ts3Data.put("slotsAmount", 64);
//        ts3Data.put("slotsAmount", ts3Api.getHostInfo().getTotalMaxClients());
        ts3Data.put("playerAmount", ts3Api.getClients().size() - 1); // minus one because the bot counts himself also
        ts3Data.put("supporterAmount", TS3ApiEnhanced.countGroupMember(504852) + TS3ApiEnhanced.countGroupMember(504894));
        ts3Data.put("youtuberAmount", TS3ApiEnhanced.countGroupMember(504858));
        ts3Data.put("streamerAmount", TS3ApiEnhanced.countGroupMember(504887));

        JSONObject games = new JSONObject();
        JSONObject ls = new JSONObject();
        ls.put("playerAmount", TS3ApiEnhanced.countGroupMember(504874) + TS3ApiEnhanced.countGroupMember(513485));
        games.put("ls", ls);

        JSONObject ets2 = new JSONObject();
        ets2.put("playerAmount", TS3ApiEnhanced.countGroupMember(504875));
        games.put("ets2", ets2);

        JSONObject rocketLeague = new JSONObject();
        rocketLeague.put("playerAmount", TS3ApiEnhanced.countGroupMember(504890));
        games.put("rocketLeague", rocketLeague);

        JSONObject mc = new JSONObject();
        mc.put("playerAmount", TS3ApiEnhanced.countGroupMember(504876));
        games.put("mc", mc);

        JSONObject pubg = new JSONObject();
        pubg.put("playerAmount", TS3ApiEnhanced.countGroupMember(504877));
        games.put("pubg", pubg);

        JSONObject rainbowSixSiege = new JSONObject();
        rainbowSixSiege.put("playerAmount", TS3ApiEnhanced.countGroupMember(504878));
        games.put("rainbowSixSiege", rainbowSixSiege);

        JSONObject gtav = new JSONObject();
        gtav.put("playerAmount", TS3ApiEnhanced.countGroupMember(504879));
        games.put("gtav", gtav);

        JSONObject conanExiles = new JSONObject();
        conanExiles.put("playerAmount", TS3ApiEnhanced.countGroupMember(515284));
        games.put("conanExiles", conanExiles);

        JSONObject forzaHorizon5 = new JSONObject();
        forzaHorizon5.put("playerAmount", TS3ApiEnhanced.countGroupMember(515285));
        games.put("forzaHorizon5", forzaHorizon5);

        JSONObject csgo = new JSONObject();
        csgo.put("playerAmount", TS3ApiEnhanced.countGroupMember(504881));
        games.put("csgo", csgo);

        JSONObject dbd = new JSONObject();
        dbd.put("playerAmount", TS3ApiEnhanced.countGroupMember(514735));
        games.put("dbd", dbd);

        JSONObject amongUs = new JSONObject();
        amongUs.put("playerAmount", TS3ApiEnhanced.countGroupMember(514736));
        games.put("amongUs", amongUs);

        JSONObject sot = new JSONObject();
        sot.put("playerAmount", TS3ApiEnhanced.countGroupMember(514737));
        games.put("sot", sot);

        JSONObject valorant = new JSONObject();
        valorant.put("playerAmount", TS3ApiEnhanced.countGroupMember(514738));
        games.put("valorant", valorant);

        JSONObject garticphone = new JSONObject();
        garticphone.put("playerAmount", TS3ApiEnhanced.countGroupMember(514739));
        games.put("garticphone", garticphone);

        ts3Data.put("games", games);

        Helper.saveJSONDate(Main.getBotPath() + "/data/ts3Data.save", ts3Data);
    }
}