package eu.projekt.eleven.ts3bot.helper;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import eu.projekt.eleven.ts3bot.Main;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class TS3ApiEnhanced
{
    private static final TS3Api ts3Api = Main.getTs3Api();

    private static HashMap<String, int[]> oldGroupHashMap = new HashMap<>();

    public static void pokeClients(JSONArray groupIds, String message)
    {
        for(Client client : getClientsByGroupIds(groupIds))
        {
            ts3Api.pokeClient(client.getId(), message);
        }
    }

    public static void pokeClients(int[] clientIds, String message)
    {
        for(int clientId : clientIds)
        {
            ts3Api.pokeClient(clientId, message);
        }
    }

    public static boolean isAFK(int clientId)
    {
        if(!ts3Api.isClientOnline(clientId))
            return false;

        Client client = ts3Api.getClientInfo(clientId);
        return isAFK(client);
    }

    public static boolean isAFK(int clientId, int idleSeconds)
    {
        if(!ts3Api.isClientOnline(clientId))
            return false;

        Client client = ts3Api.getClientInfo(clientId);
        return isAFK(client, idleSeconds);
    }

    public static boolean isAFK(String clientUid)
    {
        Client client = ts3Api.getClientByUId(clientUid);

        if(client == null)
            return false;

        return isAFK(client);
    }

    public static boolean isAFK(String clientUid, int idleSeconds)
    {
        Client client = ts3Api.getClientByUId(clientUid);

        if(client == null)
            return false;

        return isAFK(client, idleSeconds);
    }

    public static boolean isAFK(Client client)
    {
        return client.isAway() || client.isInputMuted() || client.isOutputMuted();
    }

    public static boolean isAFK(Client client, int idleSeconds)
    {
        return client.isAway() || client.isInputMuted() || client.isOutputMuted() || client.getIdleTime() > idleSeconds * 1000L;
    }

    public static boolean isBot(Client client)
    {
        return client.getUniqueIdentifier().equals(ts3Api.whoAmI().getUniqueIdentifier());
    }

    public static List<Client> getClientsByGroupIds(JSONArray groupIds)
    {
        List<Client> clients = new ArrayList<>();
        String groupIdString;

        for(Client client : ts3Api.getClients())
        {
            for(Object groupId : groupIds)
            {
                groupIdString = groupId.toString();

                if(Helper.isInteger(groupIdString) && client.isInServerGroup(Integer.parseInt(groupIdString)))
                {
                    clients.add(client);
                    break;
                }
            }
        }

        return clients;
    }

    public static HashMap<String, List<Integer>> getAllGroupChanges()
    {
        HashMap<String, List<Integer>> allGroupChangesHashMap = new HashMap<>();
        HashMap<String, int[]> tmpGroupHashMap = new HashMap<>();

        for(Client client : ts3Api.getClients())
        {
            if(oldGroupHashMap.containsKey(client.getUniqueIdentifier()))
            {
                int[] groups = oldGroupHashMap.get(client.getUniqueIdentifier());

                List<Integer> groupChanges = new ArrayList<>(CollectionUtils.disjunction(Helper.integerArrayToList(groups), Helper.integerArrayToList(client.getServerGroups())));
                if(groupChanges.size() <= 0)
                    continue;

                allGroupChangesHashMap.put(client.getUniqueIdentifier(), groupChanges);
            }

            tmpGroupHashMap.put(client.getUniqueIdentifier(), client.getServerGroups());
        }

        oldGroupHashMap.clear();
        oldGroupHashMap.putAll(tmpGroupHashMap);

        return allGroupChangesHashMap;
    }

    public static boolean hasGroup(String clientId, int groupId)
    {
        if(!ts3Api.isClientOnline(clientId))
            return false;

        for(int group : ts3Api.getClientByUId(clientId).getServerGroups())
        {
            if(groupId == group)
                return true;
        }

        return false;
    }

    public static boolean hasGroup(Client client, int groupId)
    {
        for(int group : client.getServerGroups())
        {
            if(groupId == group)
                return true;
        }

        return false;
    }

    public static boolean hasGroup(int clientId, int groupId)
    {
        Client client = getOnlineClientById(clientId);

        if(client == null)
            return false;

        for(int group : client.getServerGroups())
        {
            if(groupId == group)
                return true;
        }

        return false;
    }

    public static Client getOnlineClientById(int clientId)
    {
        for(Client client : ts3Api.getClients())
        {
            if(client.getId() == clientId)
                return client;
        }

        return null;
    }

    public static int countGroupMember(int group)
    {
        int groupMemberAmount = 0;

        for(Client client : ts3Api.getClients())
        {
            if(hasGroup(client, group))
                groupMemberAmount++;
        }

        return groupMemberAmount;
    }
}