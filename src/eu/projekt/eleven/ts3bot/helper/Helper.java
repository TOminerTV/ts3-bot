package eu.projekt.eleven.ts3bot.helper;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class Helper
{
    public static boolean isInteger(String string)
    {
        try
        {
            if(string == null)
            {
                return false;
            }

            int length = string.length();

            if(length == 0)
            {
                return false;
            }

            int i = 0;
            if(string.charAt(0) == '-')
            {
                if(length == 1)
                {
                    return false;
                }

                i = 1;
            }

            for(; i < length; i++)
            {
                char c = string.charAt(i);
                if(c < '0' || c > '9')
                {
                    return false;
                }
            }

            return true;
        }
        catch(NumberFormatException exception)
        {
            return false;
        }
    }

    public static boolean jsonArrayContainsInteger(JSONArray jsonArray, Integer integer)
    {
        for(Object object : jsonArray)
        {
            if(!isInteger(object.toString()))
                continue;

            if(Integer.parseInt(object.toString()) == integer)
                return true;
        }

        return false;
    }

    public static List<Integer> integerArrayToList(int[] integerArray)
    {
        List<Integer> integers = new ArrayList<>();

        for(Integer integer : integerArray)
        {
            integers.add(integer);
        }

        return integers;
    }

    public static void saveJSONDate(String path, JSONObject jsonData)
    {
        FileWriter fileWriter;

        try
        {
            fileWriter = new FileWriter(path);
            fileWriter.write(jsonData.toJSONString());

            fileWriter.flush();
            fileWriter.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}