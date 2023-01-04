/*
 *  Copyright 2023 Oliver Moseler
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package de.unitrier.st.codesparks.core.properties;

import de.unitrier.st.codesparks.core.CoreUtil;

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
        try
        {
            PLUGIN_PATH = CoreUtil.getPluginPathString();
        } catch (NullPointerException e)
        {
            e.printStackTrace();
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
