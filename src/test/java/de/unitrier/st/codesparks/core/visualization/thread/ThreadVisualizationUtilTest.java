/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.thread;

import org.junit.Assert;
import org.junit.Test;

public final class ThreadVisualizationUtilTest
{
    @Test
    public void testGetDiscreteXValuedScaleWidthLower20()
    {
        int discreteXValuedScaleWidth = ThreadVisualizationUtil.getDiscreteXValuedScaleWidth(5, .6, 125);
        Assert.assertEquals(75, discreteXValuedScaleWidth);
        discreteXValuedScaleWidth = ThreadVisualizationUtil.getDiscreteXValuedScaleWidth(5, .4, 125);
        Assert.assertEquals(50, discreteXValuedScaleWidth);
    }

    @Test
    public void testGetDiscreteXValuedScaleWidthGreater20()
    {
        int discreteXValuedScaleWidth = ThreadVisualizationUtil.getDiscreteXValuedScaleWidth(20, .62, 125);
        Assert.assertEquals(76, discreteXValuedScaleWidth);
        discreteXValuedScaleWidth = ThreadVisualizationUtil.getDiscreteXValuedScaleWidth(20, .38, 125);
        Assert.assertEquals(47, discreteXValuedScaleWidth);
    }

    @Test
    public void testGetDiscreteXValuedScaleWidthFullGreater20()
    {
        final int discreteXValuedScaleWidth = ThreadVisualizationUtil.getDiscreteXValuedScaleWidth(20, 1D, 125);
        Assert.assertEquals(125, discreteXValuedScaleWidth);
    }

    @Test
    public void testGetDiscreteXValuedScaleWidthFullLower20()
    {
        final int discreteXValuedScaleWidth = ThreadVisualizationUtil.getDiscreteXValuedScaleWidth(5, 1D, 125);
        Assert.assertEquals(125, discreteXValuedScaleWidth);
    }

}
