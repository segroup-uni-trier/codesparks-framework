/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.overview;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;

public class NumberOfThreadsArtifactMetricComparator extends ArtifactMetricComparator
{
    public NumberOfThreadsArtifactMetricComparator(final boolean enabled)
    {
        super(new AMetricIdentifier()
        {
            @Override
            public String getIdentifier()
            {
                return "Number-of-threads";
            }

            @Override
            public String getName()
            {
                return "Number of threads";
            }

            @Override
            public String getDisplayString()
            {
                return "Number of threads";
            }

            @Override
            public String getShortDisplayString()
            {
                return "#Threads";
            }

            @Override
            public boolean isNumerical()
            {
                return true;
            }

            @Override
            public boolean isRelative()
            {
                return false;
            }
        }, enabled);
        this.toDoubleFunction = AArtifact::getNumberOfThreads;
    }

    public NumberOfThreadsArtifactMetricComparator()
    {
        this(false);
    }
}
