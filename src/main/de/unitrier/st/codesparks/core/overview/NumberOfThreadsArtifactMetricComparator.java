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
            public Class<Double> getMetricValueType()
            {
                return Double.class;
            }

            //            @Override
//            public boolean isNumerical()
//            {
//                return true;
//            }

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
