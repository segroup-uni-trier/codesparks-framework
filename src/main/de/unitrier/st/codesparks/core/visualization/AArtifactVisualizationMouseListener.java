/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.visualization;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AMetricIdentifier;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.logging.IUserActivityLogger;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.popup.PopupPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class AArtifactVisualizationMouseListener extends MouseAdapter
{
    protected JComponent component;
    protected final Dimension dimension;
    protected final AArtifact artifact;
    protected final AMetricIdentifier primaryMetricIdentifier;

    protected AArtifactVisualizationMouseListener(
            final JComponent component
            , final Dimension dimension
            , final AArtifact artifact
            , final AMetricIdentifier primaryMetricIdentifier
    )
    {
        this.component = component;
        this.dimension = dimension;
        this.artifact = artifact;
        this.primaryMetricIdentifier = primaryMetricIdentifier;
    }

    protected abstract PopupPanel createPopupContent(AArtifact artifact); // TODO: remove parameter artifact since it is in the constructor already

    protected abstract String createPopupTitle(AArtifact artifact);

    protected void visualizationMouseClicked(MouseEvent e)
    {
        final IUserActivityLogger logger = UserActivityLogger.getInstance();
        final String identifier = artifact.getIdentifier();
        final Object source = e.getSource();
        final JBPanel<BorderLayoutPanel> popupPanelWrapper = new BorderLayoutPanel();
        final PopupPanel popupPanel = createPopupContent(artifact);

        logger.log(UserActivityEnum.PopupOpened, popupPanel.getType(), identifier);

        popupPanelWrapper.add(popupPanel, BorderLayout.CENTER);

        final String popupTitle = createPopupTitle(artifact);

        ComponentPopupBuilder componentPopupBuilder = JBPopupFactory.getInstance().
                createComponentPopupBuilder(popupPanelWrapper, null)
                .setMovable(true)
//                                    .setFocusable(true)
                .setResizable(true)
//                                    .setRequestFocus(true)
                .setMinSize(dimension)
//                                    .setShowShadow(true)
                .setCouldPin(jbPopup -> {
                    Project project = CoreUtil.getCurrentlyOpenedProject();
                    String name = LocalizationUtil.getLocalizedString("codesparks.ui.artifactpopup.displayname");
                    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                    final ToolWindow toolWindow = toolWindowManager.getToolWindow(name);
                    if (toolWindow != null)
                    {
                        toolWindow.remove();
                    }
                    JBPanel<BorderLayoutPanel> pinPanel = new BorderLayoutPanel();
                    JBPanel<BorderLayoutPanel> titlePanel = new BorderLayoutPanel();
                    titlePanel.add(new JLabel(popupTitle, JLabel.CENTER), BorderLayout.CENTER);

                    pinPanel.add(titlePanel, BorderLayout.NORTH);
                    pinPanel.add(popupPanel, BorderLayout.CENTER);

                    ToolWindow methodPopupToolWindow = toolWindowManager.registerToolWindow(new RegisterToolWindowTask(
                            name
                            , ToolWindowAnchor.RIGHT
                            , null
                            , true
                            , true
                            , true
                            , true
                            , null
                            , IconLoader.getIcon("/icons/profiling_13x12.png", getClass())
                            , () -> name
                    ));

                    ContentManager contentManager = methodPopupToolWindow.getContentManager();
                    contentManager.addContent(ContentFactory.SERVICE.getInstance()
                            .createContent(pinPanel, "", true));
                    jbPopup.cancel();
                    methodPopupToolWindow.show(() -> {});

                    logger.log(UserActivityEnum.PopupPinned, popupPanel.getType(), identifier);
                    return true;
                }).setTitle(popupTitle);
        final JBPopup popup = componentPopupBuilder.createPopup();
        popup.setSize(dimension);
        popup.pack(false, true);
        popup.canClose();
        popup.showUnderneathOf((Component) source);
        popupPanel.registerPopup(popup); // In order to be able to close the popup in case the user
        // clicked on a caller/callee to navigate to it.
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        visualizationMouseClicked(e);
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        VisualizationUtil.setCursorRecursively(component, Cursor.getPredefinedCursor(Cursor
                .HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        VisualizationUtil.setCursorRecursively(component, Cursor.getPredefinedCursor(Cursor
                .TEXT_CURSOR));
    }
}
