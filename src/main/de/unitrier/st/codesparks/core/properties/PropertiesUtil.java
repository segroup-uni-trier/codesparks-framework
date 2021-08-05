/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.properties;

import de.unitrier.st.codesparks.core.service.CodeSparksInstanceService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public final class PropertiesUtil
{
    private PropertiesUtil() {}

    private static String PLUGIN_PATH;

    static
    {
        String errMessage = "There is no implementation of the interface " + CodeSparksInstanceService.class + "! Please create an implementation " +
                "and register that implementation as 'applicationService' in the plugin-xml. See https://jetbrains" +
                ".org/intellij/sdk/docs/basics/plugin_structure/plugin_services.html";
        try
        {
            final CodeSparksInstanceService instance = CodeSparksInstanceService.getInstance();
            PLUGIN_PATH = instance.getPluginPathString();
        } catch (NullPointerException e)
        {
            System.err.println(errMessage);
            PLUGIN_PATH = "";
        }
    }

    private static String getPropertiesFilePath(String propertiesFile, boolean createOnMissing)
    {
        if (propertiesFile == null)
        {
            return null;
        }
        File file = new File(PLUGIN_PATH + File.separator + propertiesFile);
        synchronized (PropertiesUtil.class)
        {
            if (file.exists())
            {
                return file.getPath();
            }
            if (!createOnMissing)
            {
                return null;
            }
            try
            {
                boolean fileCreated = file.createNewFile();
                if (fileCreated)
                {
                    return file.getPath();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static String getPropertiesFilePath(String propertiesFile)
    {
        return getPropertiesFilePath(propertiesFile, true);
    }

    private static Properties getProperties(String propertiesFile)
    {
        String rootPath = getPropertiesFilePath(propertiesFile);
        if (rootPath == null)
        {
            return null;
        }
        Properties properties = new Properties();
        try
        {
            properties.load(new FileInputStream(rootPath));
            return properties;
        } catch (IOException ignored) {}
        return null;
    }

    public static String getPropertyValue(String propertiesFile, String key)
    {
        Properties properties = getProperties(propertiesFile);
        if (properties == null)
        {
            return null;
        }
        return properties.getProperty(key);
    }

    public static Boolean getBooleanPropertyValue(String propertiesFile, String key)
    {
        String propertiesValue = getPropertyValue(propertiesFile, key);
        if (propertiesValue == null)
        {
            return null;
        }
        try
        {
            return Boolean.valueOf(propertiesValue);
        } catch (NumberFormatException ignored)
        {
            return null;
        }
    }

    public static Boolean getBooleanPropertyValueOrDefault(String propertiesFile, String key, Boolean defaultValue)
    {
        Boolean booleanPropertiesValue = getBooleanPropertyValue(propertiesFile, key);
        if (booleanPropertiesValue == null)
        {
            setPropertyValue(propertiesFile, key, defaultValue);
            return defaultValue;
        }
        return booleanPropertiesValue;
    }

    public static Integer getIntegerPropertyValue(String propertiesFile, String key)
    {
        String propertiesValue = getPropertyValue(propertiesFile, key);
        if (propertiesValue == null)
        {
            return null;
        }
        try
        {
            return Integer.parseInt(propertiesValue);
        } catch (NumberFormatException ignored)
        {
            return null;
        }
    }

    public static Integer getIntegerPropertyValueOrDefault(String propertiesFile, String key, Integer defaultValue)
    {
        Integer integerPropertyValue = getIntegerPropertyValue(propertiesFile, key);
        if (integerPropertyValue == null)
        {
            setPropertyValue(propertiesFile, key, defaultValue);
            return defaultValue;
        }
        return integerPropertyValue;
    }

    public static void setPropertyValue(String propertiesFile, String key, Object value)
    {
        Properties properties = getProperties(propertiesFile);
        if (properties == null)
        {
            properties = new Properties();
        }
        properties.setProperty(key, value.toString());
        try
        {
            String propertiesFilePath = getPropertiesFilePath(propertiesFile);
            FileWriter fileWriter = new FileWriter(propertiesFilePath);
            properties.store(fileWriter, "Stored to properties file");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
