package de.unitrier.st.insituprofiling.core.logging;

public interface IUserActivityLogger
{
    void log(UserActivityEnum action, String... additionalInformation);

    void close();
}
