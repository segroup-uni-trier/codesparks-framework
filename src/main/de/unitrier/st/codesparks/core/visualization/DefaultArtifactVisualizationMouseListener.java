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
package de.unitrier.st.codesparks.core.visualization;

import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.visualization.popup.PopupPanel;

import javax.swing.*;
import java.awt.*;

public class DefaultArtifactVisualizationMouseListener extends AArtifactVisualizationMouseListener
{
    public DefaultArtifactVisualizationMouseListener(
            final JComponent component,
            final AArtifact artifact,
            final AMetricIdentifier primaryMetricIdentifier
    )
    {
        super(component, new Dimension(550, 70), artifact, primaryMetricIdentifier);
    }

    @Override
    protected PopupPanel createPopupContent(final AArtifact artifact)
    {
        final PopupPanel popupPanel = new PopupPanel("Default");
        final JPanel panel = new JPanel(new BorderLayout());
        final BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        final JLabel artifactLabel = new JLabel();
        artifactLabel.setText("Artifact: " + artifact.getName());
        panel.add(artifactLabel);
        final JLabel metricLabel = new JLabel();
        metricLabel.setText("Metric: " + primaryMetricIdentifier.getDisplayString());
        panel.add(metricLabel);
        final JLabel metricValueLabel = new JLabel();
        metricValueLabel.setText("Metric value: " + primaryMetricIdentifier.getValueDisplayString(artifact.getMetricValue(primaryMetricIdentifier)));
        panel.add(metricValueLabel);
        popupPanel.add(panel, BorderLayout.CENTER);
        return popupPanel;
    }

    @Override
    protected String createPopupTitle(final AArtifact artifact)
    {
        //noinspection StringBufferReplaceableByString
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(artifact.getShortName());
        stringBuilder.append(" - ");
        stringBuilder.append(primaryMetricIdentifier.getShortDisplayString());
        return stringBuilder.toString();
    }
}
