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
package de.unitrier.st.codesparks.core.data;

import de.unitrier.st.codesparks.core.CoreUtil;

public abstract class AThreadArtifact extends AArtifact
{
    /**
     * The callSiteIdentifier should contain an identifier of a method (lambda etc.) that is executed by the respective thread. That is:
     * &bull; The run method of a subclass of java.lang.Thread or anonymous and explicit Runnable implementations
     * &bull; The lambda expression. In that case the identifier has the format: &lt;classIdentifier&gt;{@literal @}&lt;lineNumber&gt;
     */
    private String callSiteIdentifier;
    private boolean filtered;

    public AThreadArtifact(final String identifier, final Class<? extends AThreadArtifact> threadArtifactClass)
    {
        super(identifier, identifier, threadArtifactClass);
        filtered = false;
    }

    public String getCallSiteIdentifier()
    {
        return callSiteIdentifier;
    }

    public void setCallSiteIdentifier(final String callSiteIdentifier)
    {
        this.callSiteIdentifier = callSiteIdentifier;
    }

    public boolean isFiltered() {return filtered;}

    /*
     This method is additionally added and used to enhance code readability depending on the concrete context.
     For instance, sometimes it makes more sense to ask whether a thread is 'not filtered' instead of 'selected' and vice versa.
     */
    public boolean isNotFiltered() {return !filtered;}

    public void setFiltered(final boolean filtered)
    {
        this.filtered = filtered;
    }

    public boolean isSelected() {return !filtered;}

    public void setSelected(final boolean selected)
    {
        this.filtered = !selected;
    }

    /**
     * Euclidean distance
     *
     * @param thr              The thread artifact to get the distance to
     * @param metricIdentifier The metric identifier. Note, has to be numerical.
     * @return The euclidean distance from this thread artifact to thr
     */
    public double dist(final AThreadArtifact thr, final AMetricIdentifier metricIdentifier)
    {
        final double numericalMetricValue = this.getNumericalMetricValue(metricIdentifier);
        final double numericalMetricValue1 = thr.getNumericalMetricValue(metricIdentifier);
        //noinspection UnnecessaryLocalVariable debugging purposes
        final double dist = Math.abs(numericalMetricValue1 - numericalMetricValue); // Euclidean distance
        return dist;
    }

    @Override
    public String getDisplayString(final AMetricIdentifier metricIdentifier, int maxLen)
    {
        String metricValueString;
        if (metricIdentifier.isNumerical())
        {
            final double metricValue = getNumericalMetricValue(metricIdentifier);
            if (metricIdentifier.isRelative())
            {
                metricValueString = CoreUtil.formatPercentageWithLeadingWhitespace(metricValue);
            } else
            {
                metricValueString = Double.toString(metricValue);
            }
        } else
        {
            metricValueString = getMetricValue(metricIdentifier).toString();
        }
        final String reduce = CoreUtil.reduceToLength(identifier, maxLen);
        return metricValueString + " " + reduce;
    }

    @Override
    public String getDisplayString(final AMetricIdentifier metricIdentifier)
    {
        return getDisplayString(metricIdentifier, 39);
    }
}
