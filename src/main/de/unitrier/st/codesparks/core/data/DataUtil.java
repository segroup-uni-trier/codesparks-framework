package de.unitrier.st.codesparks.core.data;

import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public final class DataUtil
{
    private DataUtil() {}

    //TODO: inline in method below
    private static double getThreadFilteredRelativeNumericMetricValueRatioOfArtifact(
            final AArtifact artifact
            , final IMetricIdentifier metricIdentifier
    )
    {
        if (!metricIdentifier.isNumerical() || !metricIdentifier.isRelative())
        { // The metric value is expected to be a relative numeric value, i.e. element of the closed interval [0,1]
            return Double.NaN;
        }
        return artifact.getThreadArtifacts()
                .stream()
                .filter(threadArtifact -> !threadArtifact.isFiltered())
                .mapToDouble(threadArtifact -> threadArtifact.getNumericalMetricValue(metricIdentifier))
                .reduce(0d, Double::sum);
    }

    public static double getThreadFilteredRelativeNumericMetricValueOf(
            final AArtifact artifact
            , final IMetricIdentifier metricIdentifier
    )
    {
        if (artifact == null || metricIdentifier == null)
        {
            return Double.NaN;
        }
        double metricValue = artifact.getNumericalMetricValue(metricIdentifier);
        double ratio = 1d;
        if (artifact.hasThreads())
        {
            ratio = DataUtil.getThreadFilteredRelativeNumericMetricValueRatioOfArtifact(artifact, metricIdentifier);
        }
        //noinspection UnnecessaryLocalVariable : Not inlined because of debugging reasons
        final double threadFilteredMetricValue = metricValue * ratio;
        return threadFilteredMetricValue;
    }

    public static double jaccard(ArtifactTrie t1, ArtifactTrie t2)
    {
        final Multiset<String> multiSetT1 = t1.vertexMultiSet();
        final Multiset<String> multiSetT2 = t2.vertexMultiSet();

        final Multiset<String> intersection = Multisets.intersection(multiSetT1, multiSetT2);

        final Multiset<String> union = Multisets.union(multiSetT1, multiSetT2);

        double ret = intersection.size() / (double) union.size();

        return ret;
    }
}
