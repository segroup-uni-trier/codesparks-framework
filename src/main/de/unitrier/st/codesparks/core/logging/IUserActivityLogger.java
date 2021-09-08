/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.logging;

public interface IUserActivityLogger
{
    void log(UserActivityEnum action, String... additionalInformation);

    void close();
}
