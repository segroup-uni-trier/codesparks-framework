package de.unitrier.st.codesparks.core.logging;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public interface IUserActivityLogger
{
    void log(UserActivityEnum action, String... additionalInformation);

    void close();
}
