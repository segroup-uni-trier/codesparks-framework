package de.unitrier.st.insituprofiling.core.visualization.thread;

import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.data.ThreadArtifactCluster;
import de.unitrier.st.insituprofiling.core.logging.UserActivityEnum;
import de.unitrier.st.insituprofiling.core.logging.UserActivityLogger;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RadialZoomedThreadArtifactVisualizationMouseAdapter extends MouseAdapter
{
    private final AProfilingArtifact artifact;
    private final int frameSize;
    private final IClusterHoverable clusterHover;
    private final RadialZoomedThreadArtifactVisualization threadArtifactVisualization;
    private final JPanel visualizationWrapper;

    RadialZoomedThreadArtifactVisualizationMouseAdapter(
            RadialZoomedThreadArtifactVisualization threadArtifactVisualization,
            AProfilingArtifact artifact, IClusterHoverable clusterHover, JPanel visualizationWrapper)
    {
        this.threadArtifactVisualization = threadArtifactVisualization;
        this.artifact = artifact;
        this.frameSize = RadialThreadVisualizationConstants.CIRCLE_FRAMESIZE_ZOOMED;
        this.clusterHover = clusterHover;
        this.visualizationWrapper = visualizationWrapper;
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        super.mouseMoved(e);
        int hoverCount = 0;
        for (ThreadArtifactCluster cluster : artifact.getSortedDefaultThreadArtifactClustering())
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
        x -= (visualizationWrapper.getWidth() / 2 - RadialThreadVisualizationConstants.FRAME_ZOOMED / 2);
        x = frameSize - x;
        y = frameSize - y;
        double mousePointerAngle = Math.abs(Math.toDegrees(Math.atan2(y - frameSize / 2D, x - frameSize / 2D)) - 180);
        final VisualThreadArtifactClusterProperties props =
                VisualThreadArtifactClusterPropertiesManager.getInstance().getProperties(cluster);
        RadialVisualThreadArtifactClusterProperties properties = (RadialVisualThreadArtifactClusterProperties) props;
        double distance = Math.sqrt(Math.pow(frameSize / 2D - x, 2) + Math.pow(frameSize / 2D - y, 2));

        double discreteRuntimeRatio;
        if (properties.getRuntimeRationSum() <= 0.33)
        {
            discreteRuntimeRatio = 0.33;
        } else if (properties.getRuntimeRationSum() > 0.33 && properties.getRuntimeRationSum() <= 0.66)
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
