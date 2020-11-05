package de.unitrier.st.insituprofiling.core.visualization;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.ComponentPopupBuilder;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.*;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.insituprofiling.core.CoreUtil;
import de.unitrier.st.insituprofiling.core.data.AProfilingArtifact;
import de.unitrier.st.insituprofiling.core.localization.LocalizationUtil;
import de.unitrier.st.insituprofiling.core.logging.IUserActivityLogger;
import de.unitrier.st.insituprofiling.core.logging.UserActivityEnum;
import de.unitrier.st.insituprofiling.core.logging.UserActivityLogger;
import de.unitrier.st.insituprofiling.core.visualization.popup.PopupPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class AArtifactVisualizationMouseListener extends MouseAdapter
{
    protected JComponent component;
    protected Dimension dimension;
    protected AProfilingArtifact artifact;

    protected AArtifactVisualizationMouseListener(JComponent component, Dimension dimension, AProfilingArtifact artifact)
    {
        this.component = component;
        this.dimension = dimension;
        this.artifact = artifact;
    }

    protected abstract PopupPanel createPopupContent(AProfilingArtifact artifact);

    protected abstract String createPopupTitle(AProfilingArtifact artifact);

    protected void visualizationMouseClicked(MouseEvent e)
    {
        if (artifact.getThreadArtifacts().isEmpty())
        {
            return;
        }

        final IUserActivityLogger logger = UserActivityLogger.getInstance();
        final String identifier = artifact.getIdentifier();
        final Object source = e.getSource();
        final JBPanel<BorderLayoutPanel> popupPanelWrapper = new BorderLayoutPanel();//new JBPanel<>(new BorderLayout());
//        popupPanelWrapper.setLayout(new BoxLayout(popupPanelWrapper, BoxLayout.Y_AXIS));
        final PopupPanel popupPanel = createPopupContent(artifact);

        logger.log(UserActivityEnum.PopupOpened, popupPanel.getType(), identifier);

        popupPanelWrapper.add(popupPanel, BorderLayout.CENTER);

        // Navigate to artifact button
//        final JBPanel navigateToArtifactButtonWrapper = new JBPanel(new BorderLayout());
//        navigateToArtifactButtonWrapper.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
//        final JButton navigateToArtifactButton = new JButton(
//                LocalizationUtil.getLocalizedString("profiling.ui.artifact.popup.button.navigate.to")
//                        + ": " + artifact.getDisplayString(70));
//        navigateToArtifactButtonWrapper.add(navigateToArtifactButton, BorderLayout.CENTER);
//
//        popupPanelWrapper.add(navigateToArtifactButtonWrapper, BorderLayout.SOUTH);

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
                    String name = LocalizationUtil.getLocalizedString("profiling.ui.artifactpopup.displayname");
                    ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
                    final ToolWindow toolWindow = toolWindowManager.getToolWindow(name);
                    if (toolWindow != null)
                    {
//                        toolWindowManager.unregisterToolWindow(name);
                        toolWindow.remove();
                    }
                    JBPanel<BorderLayoutPanel> pinPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
                    JBPanel<BorderLayoutPanel> titlePanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
                    titlePanel.add(new JLabel(popupTitle, JLabel.CENTER), BorderLayout.CENTER);

                    pinPanel.add(titlePanel, BorderLayout.NORTH);
                    pinPanel.add(popupPanel, BorderLayout.CENTER);
//                    pinPanel.add(navigateToArtifactButton, BorderLayout.SOUTH);

//                    ToolWindow methodPopupToolWindow =
//                            toolWindowManager.registerToolWindow(name, true, ToolWindowAnchor.RIGHT);

                    ToolWindow methodPopupToolWindow = toolWindowManager.registerToolWindow(new RegisterToolWindowTask(
                            name
                            , ToolWindowAnchor.RIGHT
                            , null
                            , true
                            , true
                            , true
                            , true
                            , null
                            , IconLoader.getIcon("/icons/profiling_13x12.png")
                            , () -> name
                    ));

                    //methodPopupToolWindow.setIcon(IconLoader.getIcon("/icons/profiling_16x15.png"));
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
//        navigateToArtifactButton.addActionListener(event -> {
//            CoreUtil.navigate(identifier);
//            popup.cancel();
//        });
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
