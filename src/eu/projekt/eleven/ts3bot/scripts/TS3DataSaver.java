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
        ts3Data.put("supporterAmount", TS3ApiEnhanced.countGroupMember(515494) + TS3ApiEnhanced.countGroupMember(515531));
        ts3Data.put("youtuberAmount", TS3ApiEnhanced.countGroupMember(515499));
        ts3Data.put("streamerAmount", TS3ApiEnhanced.countGroupMember(515526));

        JSONObject games = new JSONObject();
        JSONObject ls = new JSONObject();
        ls.put("playerAmount", TS3ApiEnhanced.countGroupMember(515513) + TS3ApiEnhanced.countGroupMember(515536));
        games.put("ls", ls);

        JSONObject ets2 = new JSONObject();
        ets2.put("playerAmount", TS3ApiEnhanced.countGroupMember(515514));
        games.put("ets2", ets2);

        JSONObject rocketLeague = new JSONObject();
        rocketLeague.put("playerAmount", TS3ApiEnhanced.countGroupMember(515529));
        games.put("rocketLeague", rocketLeague);

        JSONObject mc = new JSONObject();
        mc.put("playerAmount", TS3ApiEnhanced.countGroupMember(515515));
        games.put("mc", mc);

        JSONObject pubg = new JSONObject();
        pubg.put("playerAmount", TS3ApiEnhanced.countGroupMember(515516));
        games.put("pubg", pubg);

        JSONObject rainbowSixSiege = new JSONObject();
        rainbowSixSiege.put("playerAmount", TS3ApiEnhanced.countGroupMember(515517));
        games.put("rainbowSixSiege", rainbowSixSiege);

        JSONObject gtav = new JSONObject();
        gtav.put("playerAmount", TS3ApiEnhanced.countGroupMember(515518));
        games.put("gtav", gtav);

        JSONObject conanExiles = new JSONObject();
        conanExiles.put("playerAmount", TS3ApiEnhanced.countGroupMember(515544));
        games.put("conanExiles", conanExiles);

        JSONObject forzaHorizon5 = new JSONObject();
        forzaHorizon5.put("playerAmount", TS3ApiEnhanced.countGroupMember(515545));
        games.put("forzaHorizon5", forzaHorizon5);

        JSONObject csgo = new JSONObject();
        csgo.put("playerAmount", TS3ApiEnhanced.countGroupMember(515520));
        games.put("csgo", csgo);

        JSONObject dbd = new JSONObject();
        dbd.put("playerAmount", TS3ApiEnhanced.countGroupMember(515538));
        games.put("dbd", dbd);

        JSONObject amongUs = new JSONObject();
        amongUs.put("playerAmount", TS3ApiEnhanced.countGroupMember(515539));
        games.put("amongUs", amongUs);

        JSONObject sot = new JSONObject();
        sot.put("playerAmount", TS3ApiEnhanced.countGroupMember(515540));
        games.put("sot", sot);

        JSONObject valorant = new JSONObject();
        valorant.put("playerAmount", TS3ApiEnhanced.countGroupMember(515541));
        games.put("valorant", valorant);

        JSONObject garticphone = new JSONObject();
        garticphone.put("playerAmount", TS3ApiEnhanced.countGroupMember(515542));
        games.put("garticphone", garticphone);

        ts3Data.put("games", games);

        Helper.saveJSONDate(Main.getBotPath() + "/data/ts3Data.save", ts3Data);
    }
}