package de.unitrier.st.codesparks.core.logging;

import com.intellij.openapi.project.Project;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerLogEnum;
import de.unitrier.st.codesparks.core.editorcoverlayer.EditorCoverLayerManager;
import de.unitrier.st.codesparks.core.editorcoverlayer.IEditorCoverLayerLogger;
import de.unitrier.st.codesparks.core.properties.PropertiesFile;
import de.unitrier.st.codesparks.core.properties.PropertiesUtil;
import de.unitrier.st.codesparks.core.properties.PropertyKey;
import org.apache.commons.lang.ObjectUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.GregorianCalendar;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class UserActivityLogger implements IUserActivityLogger, IEditorCoverLayerLogger
{
    private BufferedWriter bw;

    private boolean isSetup()
    {
        synchronized (this)
        {
            return bw != null;
        }
    }

    private void setup()
    {
        if (isSetup())
        {
            return;
        }
        synchronized (this)
        {
            final Project project = CoreUtil.getCurrentlyOpenedProject();

            Calendar calendar = new GregorianCalendar();
            String logFilePath =
                    System.getProperty("user.home")
                            .concat(File.separator)
                            .concat(".codesparks")
                            .concat(File.separator)
                            .concat("user-activity-logging")
                            .concat(File.separator)
                            .concat(project.getName())
                            .concat(File.separator);

            Path path = Paths.get(logFilePath);
            try
            {
                if (!Files.exists(path))
                {
                    Files.createDirectories(path);
                }
                long time = calendar.getTime().getTime();
                File file = new File(logFilePath.concat(String.valueOf(time)).concat("_user_activity.log"));
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            EditorCoverLayerManager editorCoverLayerManager = EditorCoverLayerManager.getInstance(project);
            editorCoverLayerManager.registerLogger(this);
        }
    }

    private static UserActivityLogger instance;

    public static UserActivityLogger getInstance()
    {
        if (instance == null)
        {
            synchronized (UserActivityLogger.class)
            {
                if (instance == null)
                {
                    instance = new UserActivityLogger();
                }
            }
        }
        return instance;
    }

    private boolean checkEnabled()
    {
        Boolean userActivityLoggingEnabled = PropertiesUtil.getBooleanPropertyValueOrDefault(
                PropertiesFile.USER_INTERFACE_PROPERTIES, PropertyKey.USER_ACTIVITY_LOGGING_ENABLED, true);

        if (!userActivityLoggingEnabled)
        {
            CodeSparksLogger.addText("User Activity Logging is disabled. Please visit CodeSparks settings to enable.");
        }

        return userActivityLoggingEnabled;
    }

    private UserActivityLogger()
    {
        checkEnabled();
    }


    @Override
    public void log(UserActivityEnum action, String... additionalInformation)
    {
        try
        {
            if (!checkEnabled())
            {
                return;
            }
            setup();

            log(String.valueOf(action), additionalInformation);
        } catch (NullPointerException e)
        { // Might happen, when the com.intellij.openapi.project.ProjectManager cannot be instantiated
            // ignored
        }
    }

    @Override
    public void log(EditorCoverLayerLogEnum action, String... additionalInformation)
    {
        try
        {
            if (!checkEnabled())
            {
                return;
            }
            setup();

            log(String.valueOf(action), additionalInformation);
        }catch (NullPointerException e)
        { // Might happen, when the com.intellij.openapi.project.ProjectManager cannot be instantiated
            // ignored
        }
    }

    private void log(String actionString, String... additionalInformation)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(System.nanoTime());
        stringBuilder.append(";");
        stringBuilder.append(actionString);
        stringBuilder.append(";");
        for (String s : additionalInformation)
        {
            stringBuilder.append(s);
            stringBuilder.append(";");
        }
        stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
        log(stringBuilder.toString());
    }

    @Override
    public void close()
    {
        if (bw == null)
        {
            return;
        }
        try
        {
            bw.flush();
            bw.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void log(String str)
    {
        try
        {
            bw.write(str);
            bw.newLine();
            bw.flush();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}