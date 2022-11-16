/*
 * Copyright (c) 2022. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.wm.RegisterToolWindowTask;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.Processor;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.CodeSparksFlowManager;
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

    protected abstract PopupPanel createPopupContent(AArtifact artifact);

    protected abstract String createPopupTitle(AArtifact artifact);

    protected void visualizationMouseClicked(final MouseEvent e)
    {
        final IUserActivityLogger logger = UserActivityLogger.getInstance();
        final String identifier = artifact.getIdentifier();

        final JBPanel<BorderLayoutPanel> popupPanelWrapper = new BorderLayoutPanel();
        final PopupPanel popupPanel = createPopupContent(artifact);

        logger.log(UserActivityEnum.PopupOpened, popupPanel.getDescription(), identifier);

        final JBPanel<BorderLayoutPanel> controlButtonsWrapper = new JBPanel<>();
        final JPanel controlButtonsBox = new JPanel();
        controlButtonsBox.setLayout(new BoxLayout(controlButtonsBox, BoxLayout.X_AXIS));
        controlButtonsBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        controlButtonsBox.setAlignmentY(Component.TOP_ALIGNMENT);

        controlButtonsWrapper.add(controlButtonsBox, BorderLayout.CENTER);

        popupPanelWrapper.add(controlButtonsWrapper, BorderLayout.SOUTH);
        popupPanelWrapper.add(popupPanel, BorderLayout.CENTER);

        final String popupTitle = createPopupTitle(artifact);

        final Processor<JBPopup> pinProcessor = jbPopup -> {
            final JBPanel<BorderLayoutPanel> pinPanel = new BorderLayoutPanel();
            final JBPanel<BorderLayoutPanel> titlePanel = new BorderLayoutPanel();
            titlePanel.add(new JLabel(popupTitle, JLabel.CENTER), BorderLayout.CENTER);

            pinPanel.add(titlePanel, BorderLayout.NORTH);
            pinPanel.add(popupPanel, BorderLayout.CENTER);

            final String name = LocalizationUtil.getLocalizedString("codesparks.ui.artifactpopup.displayname");
            final Project project = CoreUtil.getCurrentlyOpenedProject();
            assert project != null;
            final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            ToolWindow popupToolWindow = toolWindowManager.getToolWindow(name);
            if (popupToolWindow == null)
            {
                final CodeSparksFlowManager codeSparksFlowManager = CodeSparksFlowManager.getInstance();
                final ImageIcon imageIcon = codeSparksFlowManager.getImageIcon();
                //noinspection UnstableApiUsage
                popupToolWindow = toolWindowManager.registerToolWindow(new RegisterToolWindowTask(
                        name
                        , ToolWindowAnchor.RIGHT
                        , null
                        , true
                        , true
                        , true
                        , true
                        , null
                        , imageIcon
                        , () -> "CodeSparks " + name
                ));
            }

            final ContentManager contentManager = popupToolWindow.getContentManager();
            final ContentFactory contentFactory = ContentFactory.getInstance();
            final String shortName = artifact.getShortName();
            final Content content = contentFactory.createContent(pinPanel, shortName, true);
            final Content formerContent = contentManager.findContent(shortName);
            if (formerContent != null)
            {
                contentManager.removeContent(formerContent, true);
            }
            contentManager.addContent(content);
            contentManager.setSelectedContent(content);

            jbPopup.cancel();
            popupToolWindow.show(() -> {});

            logger.log(UserActivityEnum.PopupPinned, popupPanel.getDescription(), identifier);
            return true;
        };

        final ComponentPopupBuilder componentPopupBuilder = JBPopupFactory.getInstance().
                createComponentPopupBuilder(popupPanelWrapper, popupPanelWrapper)
                .setTitle(popupTitle)
                .setMovable(true)
                //.setShowBorder(true)
                .setResizable(true)
                .setMinSize(dimension)
                /* Experiments */
                //.setFocusable(true)
                //.setRequestFocus(true)
                //.setShowShadow(true)
                //.setMayBeParent(true)
                //.setLocateByContent(true)
                //.setBelongsToGlobalPopupStack(true)
                //.setCancelKeyEnabled(true)
                //.setNormalWindowLevel(true)
                /* There are other cancel strategies possible: */
                //.setCancelOnOtherWindowOpen(true)
                //.setCancelOnClickOutside(false)
                //.setCancelOnWindowDeactivation(true)
                //.setCancelOnMouseOutCallback(mouseEvent -> true)
                .setCancelCallback(() -> {
                    //System.out.println("should cancel?");
                    return true;
                })
                .setCouldPin(pinProcessor);
        final JBPopup popup = componentPopupBuilder.createPopup();
        popup.setSize(dimension);
        popup.setMinimumSize(dimension);
        popup.pack(false, true);
        popup.setRequestFocus(true); // Must! be set to true in order to make 'setCouldPin' work!

        final Component source = (Component) e.getSource();
        popup.showUnderneathOf(source);

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
        VisualizationUtil.setCursorRecursively(component, Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        VisualizationUtil.setCursorRecursively(component, Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }
}
