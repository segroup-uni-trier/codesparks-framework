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

import java.io.Serializable;

interface IMetricIdentifier extends Serializable
{
    String getIdentifier();

    String getName();

    String getDisplayString();

    String getShortDisplayString();

    String getValueDisplayString(Object metricValue);

    Class<?> getMetricValueType();

    /**
     * Determines if the metric is designed to represent a numerical value, i.e. its data type is a double.
     *
     * @return true if the metric represents a numeric value, false otherwise.
     */
    boolean isNumerical();

    /**
     * Determines if the metric is designed to represent a relative numerical value, i.e. its value is of the closed interval [0,1].
     *
     * @return true if the metric represents a relative numerical value, false otherwise.
     */
    boolean isRelative();
}
