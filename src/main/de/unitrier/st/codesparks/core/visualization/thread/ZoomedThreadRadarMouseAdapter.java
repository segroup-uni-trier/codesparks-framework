package de.unitrier.st.codesparks.core.visualization.thread;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.IMetricIdentifier;
import de.unitrier.st.codesparks.core.data.ThreadArtifactCluster;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/*
 * Copyright (c), Oliver Moseler, 2020
 */
public class ZoomedThreadRadarMouseAdapter extends MouseAdapter
{
    private final AArtifact artifact;
    private final IMetricIdentifier metricIdentifier;
    private final int frameSize;
    private final IClusterHoverable clusterHover;
    private final ZoomedThreadRadar threadArtifactVisualization;
    private final JPanel visualizationWrapper;

    ZoomedThreadRadarMouseAdapter(
            final ZoomedThreadRadar threadArtifactVisualization
            , final AArtifact artifact
            , final IMetricIdentifier metricIdentifier
            , final IClusterHoverable clusterHover
            , final JPanel visualizationWrapper
    )
    {
        this.threadArtifactVisualization = threadArtifactVisualization;
        this.artifact = artifact;
        this.metricIdentifier = metricIdentifier;
        this.frameSize = ThreadRadarConstants.CIRCLE_FRAMESIZE_ZOOMED;
        this.clusterHover = clusterHover;
        this.visualizationWrapper = visualizationWrapper;
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        super.mouseMoved(e);
        int hoverCount = 0;
        for (ThreadArtifactCluster cluster : artifact.getSortedDefaultThreadArtifactClustering(metricIdentifier))
        {
            if (isPointInArc(e.getX(), e.getY(), cluster))
            {
                hoverCount++;
                UserActivityLogger.getInstance().log(UserActivityEnum.ThreadClusterHovered, "clusterId=" + cluster.getId(),
                        String.valueOf(cluster));
            }
        }
        if (hoverCount == 0)
        {
            threadArtifactVisualization.unHoverCluster();
            clusterHover.onExit();
        }
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        super.mouseExited(e);
        clusterHover.onExit();
    }

    private boolean isPointInArc(int x, int y, ThreadArtifactCluster cluster)
    {
        x -= (visualizationWrapper.getWidth() / 2 - ThreadRadarConstants.FRAME_ZOOMED / 2);
        x = frameSize - x;
        y = frameSize - y;
        double mousePointerAngle = Math.abs(Math.toDegrees(Math.atan2(y - frameSize / 2D, x - frameSize / 2D)) - 180);
        final VisualThreadClusterProperties props =
                VisualThreadClusterPropertiesManager.getInstance().getProperties(cluster);
        RadialVisualThreadClusterProperties properties = (RadialVisualThreadClusterProperties) props;
        double distance = Math.sqrt(Math.pow(frameSize / 2D - x, 2) + Math.pow(frameSize / 2D - y, 2));

        double discreteRuntimeRatio;
        if (properties.getNumericalMetricRationSum() <= 0.33)
        {
            discreteRuntimeRatio = 0.33;
        } else if (properties.getNumericalMetricRationSum() > 0.33 && properties.getNumericalMetricRationSum() <= 0.66)
        {
            discreteRuntimeRatio = 0.66;
        } else
        {
            discreteRuntimeRatio = 0.97f;
        }

        final double arcAngle = properties.getArcAngle();
        final double startAngle = properties.getArcStartAngle();
        final double endAngle = (startAngle + properties.getArcAngle()) % 360;

        if (arcAngle == 0)
            return false;

        if (distance > (frameSize / 2D) * discreteRuntimeRatio)
            return false;

        if (startAngle >= endAngle)
        {
            if (startAngle <= mousePointerAngle && mousePointerAngle <= 360 || 0 <= mousePointerAngle && mousePointerAngle <= endAngle)
            {
                clusterHover.onHover(cluster);
                return true;
            }
        } else
        {
            if (startAngle <= mousePointerAngle && mousePointerAngle <= endAngle)
            {
                clusterHover.onHover(cluster);
                return true;
            }
        }
        return false;
    }
}
