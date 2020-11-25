package de.unitrier.st.codesparks.core.data;

/**
 * Attention! Not in use yet. In future, it will replace the metricIdentifier which is solely represented as String currently.
 */
public interface IMetricIdentifier
{
    String getName();

    String getDisplayString();

    boolean isNumerical();
}
