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

package de.unitrier.st.codesparks.core;

public final class MathUtil
{
    private MathUtil() {}

    public static double linearInterpolation(double f0, double f1, double x0, double x1, double x)
    {
        //noinspection UnnecessaryLocalVariable
        final double val = f0 + ((f1 - f0) / (x1 - x0)) * (x - x0);
        return val;
    }

}
