/*
 * Copyright (c) 2021. Oliver Moseler
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
