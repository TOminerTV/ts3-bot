package eu.projekt.eleven.ts3bot.helper;

import eu.projekt.eleven.ts3bot.Main;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ConfigManager
{
    JSONObject config = null;

    public ConfigManager(String configFile)
    {
        try
        {
            if(new File(Main.getBotPath() + "/configs/" + configFile + ".json").exists())
            {
                JSONParser jsonParser = new JSONParser();
                config = (JSONObject) jsonParser.parse(new FileReader(Main.getBotPath() + "/configs/" + configFile + ".json"));
            }
        }
        catch(IOException | ParseException exception)
        {
            System.err.println(UnixSystemColors.RED + "[HELPER :: CONFIG-MANAGER] Script Error: " + exception.getMessage());
        }
    }

    public <T> T getValue(String value, Class<T> type)
    {
        if(config == null || type.isPrimitive())
            return null;

        Object object = ((JSONObject) config.get(value)).get("value");

        try
        {
            if(Byte.class.equals(type))
            {
                return type.cast(new Byte(object.toString()));
            }
            else if(Short.class.equals(type))
            {
                return type.cast(new Short(object.toString()));
            }
            else if(Integer.class.equals(type))
            {
                return type.cast(new Integer(object.toString()));
            }
            else if(Long.class.equals(type))
            {
                return type.cast(new Long(object.toString()));
            }
            else if(Float.class.equals(type))
            {
                return type.cast(new Float(object.toString()));
            }
            else if(Double.class.equals(type))
            {
                return type.cast(new Double(object.toString()));
            }
            else if(Boolean.class.equals(type))
            {
                return type.cast(Boolean.parseBoolean(object.toString()));
            }
            else if(Character.class.equals(type))
            {
                return type.cast(object.toString().charAt(0));
            }
            else
            {
                return type.cast(object);
            }
        }
        catch(ClassCastException | NumberFormatException exception)
        {
            return null;
        }
    }
}