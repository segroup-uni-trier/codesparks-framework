/*
 * Copyright (c), Oliver Moseler, 2021
 */
package de.unitrier.st.codesparks.core.overview;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.*;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.CodeSparksFlowManager;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.AThreadArtifact;
import de.unitrier.st.codesparks.core.data.GlobalResetThreadArtifactFilter;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.logging.IUserActivityLogger;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.visualization.AArtifactVisualizationLabelFactory;
import de.unitrier.st.codesparks.core.visualization.AVisualizationSequence;
import de.unitrier.st.codesparks.core.visualization.BottomFlowLayout;
import de.unitrier.st.codesparks.core.visualization.popup.MetricTable;
import de.unitrier.st.codesparks.core.visualization.popup.MetricTableCellRenderer;
import de.unitrier.st.codesparks.core.visualization.popup.MetricTableMouseMotionAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ArtifactOverview
{
    /*
     * Instantiation
     */

    private static volatile ArtifactOverview instance;

    public static ArtifactOverview getInstance()
    {
        if (instance == null)
        {
            synchronized (ArtifactOverview.class)
            {
                if (instance == null)
                {
                    instance = new ArtifactOverview();
                }
            }
        }
        return instance;
    }

    private ArtifactOverview() { setupUI(); }

    private void setupUI()
    {
        rootPanel = new BorderLayoutPanel();

        /*
         * The filter panel is placed in the NORTH of the root panel
         */

        final JBPanel<BorderLayoutPanel> filterPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
        final JBPanel<BorderLayoutPanel> filterPanelWrapper = new JBPanel<>();
        filterPanelWrapper.setLayout(new BoxLayout(filterPanelWrapper, BoxLayout.Y_AXIS));

        /*
         * The filter panel contains one the one hand the filter by identifier elements, i.e. pure text filters
         */
        final JBPanel<BorderLayoutPanel> filterByIdentifierPanel = new JBPanel<>();
        filterByIdentifierPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Filter artifacts by identifier (separate with comma)"));

        filterByIdentifierPanel.setLayout(new BoxLayout(filterByIdentifierPanel, BoxLayout.Y_AXIS));

        excludeFilter = new JBTextField();
        final JBPanel<BorderLayoutPanel> excludeFilterPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
        excludeFilterPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        final JBPanel<BorderLayoutPanel> excludeFilterPanelWrapper = new JBPanel<>();
        excludeFilterPanelWrapper.setLayout(new BoxLayout(excludeFilterPanelWrapper, BoxLayout.X_AXIS));
        excludeFilterPanelWrapper.add(new JBLabel("Exclude: "));
        excludeFilterPanelWrapper.add(excludeFilter);
        excludeFilterPanel.add(excludeFilterPanelWrapper, BorderLayout.CENTER);
        filterByIdentifierPanel.add(excludeFilterPanel);

        includeFilter = new JBTextField();
        final JBPanel<BorderLayoutPanel> includeFilterPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
        includeFilterPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        final JBPanel<BorderLayoutPanel> includeFilterPanelWrapper = new JBPanel<>();
        includeFilterPanelWrapper.setLayout(new BoxLayout(includeFilterPanelWrapper, BoxLayout.X_AXIS));
        includeFilterPanelWrapper.add(new JBLabel("Include: "));
        includeFilterPanelWrapper.add(Box.createRigidArea(new Dimension(2, 0)));
        includeFilterPanelWrapper.add(includeFilter);
        includeFilterPanel.add(includeFilterPanelWrapper, BorderLayout.CENTER);
        filterByIdentifierPanel.add(includeFilterPanel);


        final JBPanel<BorderLayoutPanel> filterArtifactButtonsPanelWrapper = new JBPanel<>();
        filterArtifactButtonsPanelWrapper.setLayout(new BoxLayout(filterArtifactButtonsPanelWrapper, BoxLayout.X_AXIS));

        final JBPanel<BorderLayoutPanel> applyArtifactFilterButtonPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
        applyArtifactFilterButtonPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 2));
        final JButton applyArtifactFilterButton = new JButton(
                LocalizationUtil.getLocalizedString("codesparks.ui.overview.button.apply.artifact.filter"));
        applyArtifactFilterButtonPanel.add(applyArtifactFilterButton, BorderLayout.CENTER);

        final JBPanel<BorderLayoutPanel> resetArtifactFilterPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
        resetArtifactFilterPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 2));
        final JButton resetArtifactFilterButton = new JButton(
                LocalizationUtil.getLocalizedString("codesparks.ui.overview.button.apply.artifact.filter.reset"));
        resetArtifactFilterPanel.add(resetArtifactFilterButton, BorderLayout.CENTER);

        filterArtifactButtonsPanelWrapper.add(resetArtifactFilterPanel);
        filterArtifactButtonsPanelWrapper.add(applyArtifactFilterButtonPanel);

        filterByIdentifierPanel.add(filterArtifactButtonsPanelWrapper/*, FlowLayout.TRAILING*/);

        /*
         * Filter artifact checkboxes, i.e. standard-library filter and current-opened-file filter
         */
        final JBPanel<BorderLayoutPanel> filterCheckboxesPanel = new BorderLayoutPanel();// new JBPanel(new BorderLayout());
        filterCheckboxesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 2));
        final JBPanel<BorderLayoutPanel> filterCheckboxesPanelWrapper = new JBPanel<>();
        filterCheckboxesPanelWrapper.setLayout(new BoxLayout(filterCheckboxesPanelWrapper, BoxLayout.X_AXIS));

        filterCheckboxesPanelWrapper.add(new JBLabel("Predefined filters: "));
        filterCheckboxesPanelWrapper.add(Box.createRigidArea(new Dimension(10, 0)));
        currentFileFilter = new JCheckBox("Include current editor's artifacts only.");
        currentFileFilter.setSelected(false);
        currentFileFilter.setEnabled(false);
        standardLibraryFilter = new JCheckBox("Exclude standard library.");
        standardLibraryFilter.setSelected(true);
        standardLibraryFilter.setEnabled(false);
        filterCheckboxesPanelWrapper.add(currentFileFilter);
        filterCheckboxesPanelWrapper.add(Box.createRigidArea(new Dimension(10, 0)));
        filterCheckboxesPanelWrapper.add(standardLibraryFilter);
        filterCheckboxesPanelWrapper.add(Box.createVerticalGlue());
        filterCheckboxesPanel.add(filterCheckboxesPanelWrapper, BorderLayout.CENTER);

        filterByIdentifierPanel.add(filterCheckboxesPanel);
        filterPanelWrapper.add(filterByIdentifierPanel);

        /*
         * Thread clusters and filter panel
         */

        threadsPanel = new BorderLayoutPanel();
        threadsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Thread clusters and filtering"));
        final JBPanel<BorderLayoutPanel> threadsPanelWrapper = new JBPanel<>();
        threadsPanelWrapper.setLayout(new BoxLayout(threadsPanelWrapper, BoxLayout.Y_AXIS));

//        final JBPanel<BorderLayoutPanel> threadClusterPanel = new JBPanel<>();
//        threadClusterPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 2));
//        threadClusterPanel.setLayout(new BoxLayout(threadClusterPanel, BoxLayout.Y_AXIS));

        // Number of clusters panel
//        final JBPanel<BorderLayoutPanel> numberOfClustersPanel = new JBPanel<>();
//        numberOfClustersPanel.setLayout(new BoxLayout(numberOfClustersPanel, BoxLayout.X_AXIS));

//        final JBLabel jbLabel = new JBLabel("Compute a maximum number of (k) clusters: ");
//        numberOfClustersPanel.add(jbLabel);

//        final ComboBox<Integer> numberOfClustersComboBox = new ComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6});
//        numberOfClustersComboBox.setSelectedIndex(2);
//        numberOfClustersPanel.add(numberOfClustersComboBox);

//        threadClusterPanel.add(numberOfClustersPanel);

        // Cluster selection strategy for in-situ visualization
//        final JBPanel<BorderLayoutPanel> clusterSelectionPanel = new JBPanel<>();
//        clusterSelectionPanel.setLayout(new BoxLayout(clusterSelectionPanel, BoxLayout.X_AXIS));

//        final JBLabel jbLabel1 = new JBLabel("In-situ visualization cluster selection: ");
//        clusterSelectionPanel.add(jbLabel1);

//        final ComboBox<String> clusterSelectionComboBox = new ComboBox<>(new String[]{"TODO-1", "TODO-2", "TODO-3"});
//        clusterSelectionComboBox.setEnabled(false);
//        clusterSelectionPanel.add(clusterSelectionComboBox);

//        threadClusterPanel.add(clusterSelectionPanel);

        //        numberOfClustersComboBox.addItemListener(e -> {
//            final int stateChange = e.getStateChange();
//            final Integer item = (Integer) e.getItem();
//            if (stateChange == ItemEvent.SELECTED)
//            {
//                clusterSelectionComboBox.setEnabled(item > 3);
//            }
//        });

        // Add the cluster panel
//        threadsPanelWrapper.add(threadClusterPanel);

        /*

         */


        // TODO: Only for the clex study!
        //threadsPanel.setVisible(false);

        final JBPanel<BorderLayoutPanel> threadFilterPanel = new JBPanel<>();
        threadFilterPanel.setLayout(new BoxLayout(threadFilterPanel, BoxLayout.X_AXIS));

        programArtifactVisualizationPanel = new JBPanel<>();
        threadFilterPanel.add(programArtifactVisualizationPanel);
        // Horizontal glue has no effect
//        threadFilterPanel.add(Box.createHorizontalGlue());

        final JBPanel<BorderLayoutPanel> threadFilterControlsPanel = new JBPanel<>();// new JBPanel(new BorderLayout());
        threadFilterControlsPanel.setLayout(new BoxLayout(threadFilterControlsPanel, BoxLayout.Y_AXIS));

        final JButton resetThreadFilterButton = new JButton(LocalizationUtil.getLocalizedString("codesparks.ui.button.reset.thread.filter.global"));
        //resetThreadFilterButton.setMaximumSize(new Dimension(50, 30));
        resetThreadFilterButton.addActionListener(e -> {
            UserActivityLogger.getInstance().log(UserActivityEnum.OverviewThreadFilterReset);
            CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow().applyThreadArtifactFilter(GlobalResetThreadArtifactFilter
                    .getInstance());
        });
        final JBPanel<BorderLayoutPanel> resetThreadFilterButtonWrapper = new BorderLayoutPanel();// new JBPanel(new BorderLayout());
        resetThreadFilterButtonWrapper.add(resetThreadFilterButton, BorderLayout.CENTER);


        threadFilterControlsPanel.add(resetThreadFilterButtonWrapper);
        threadFilterControlsPanel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        threadFilterPanel.add(threadFilterControlsPanel);


        // TODO: thread state filters are still disabled
        if (threadStateFilterWrapper == null)
        {
            threadStateFilterWrapper = new BorderLayoutPanel();
        }
        threadFilterPanel.add(threadStateFilterWrapper);

        threadsPanelWrapper.add(threadFilterPanel);

        threadsPanel.add(threadsPanelWrapper, BorderLayout.CENTER);
        filterPanelWrapper.add(threadsPanel);
        filterPanel.add(filterPanelWrapper, BorderLayout.CENTER);
        rootPanel.add(filterPanel, BorderLayout.NORTH);

        /*
         * The results panel in the CENTER. The results panel contains the tabbed pane of the artifacts.
         */

        final JBPanel<BorderLayoutPanel> resultsPanel = new BorderLayoutPanel();// new JBPanel(new BorderLayout());
        tabbedPane = new JBTabbedPane();
        tabbedPaneChangeListener = new ArtifactTabbedPaneChangeListener(tabbedPane);

        resultsPanel.add(tabbedPane, BorderLayout.CENTER);
        rootPanel.add(resultsPanel, BorderLayout.CENTER);

        final ActionListener actionListener = e -> filterOverView();


        excludeFilter.addActionListener(actionListener);
        includeFilter.addActionListener(actionListener);
        applyArtifactFilterButton.addActionListener(actionListener);
        currentFileFilter.addActionListener(
                e -> {
                    boolean selected = currentFileFilter.isSelected();

                    String s = !selected ? "disabled" : "enabled";

                    UserActivityLogger.getInstance().log(UserActivityEnum.OverviewArtifactFilterCurrentEditorArtifactsOnlyChecked, s);

                    filterOverView();
                }
        );
        standardLibraryFilter.addActionListener(
                e -> {
                    boolean selected = standardLibraryFilter.isSelected();

                    String s = !selected ? "disabled" : "enabled";

                    UserActivityLogger.getInstance().log(UserActivityEnum.OverviewArtifactFilterExcludeStandardLibraryChecked, s);

                    filterOverView();
                }
        );

        resetArtifactFilterButton.addActionListener(e -> {
            UserActivityLogger.getInstance().log(UserActivityEnum.OverviewArtifactFilterReset);
            excludeFilter.setText("");
            includeFilter.setText("");
            if (currentFileArtifactFilter != null)
            {
                currentFileFilter.setSelected(false);
            }
            if (standardLibraryArtifactFilter != null)
            {
                standardLibraryFilter.setSelected(true);
            }
            filterOverView();
        });
    }

    public JPanel getRootPanel()
    {
        return this.rootPanel;
    }

    private JBPanel<BorderLayoutPanel> programArtifactVisualizationPanel;
    private JCheckBox currentFileFilter;
    private JCheckBox standardLibraryFilter;
    private JBPanel<BorderLayoutPanel> rootPanel;
    private JBPanel<BorderLayoutPanel> threadsPanel;
    private JBPanel<BorderLayoutPanel> threadStateFilterWrapper;
    private JBTabbedPane tabbedPane;
    private ChangeListener tabbedPaneChangeListener;
    private JBTextField excludeFilter;
    private JBTextField includeFilter;

    /*
     * Artifact tabbed pane change listener inner class
     */

    private static class ArtifactTabbedPaneChangeListener implements ChangeListener
    {
        private final JBTabbedPane tabbedPane;
        private final IUserActivityLogger userActivityLogger;

        ArtifactTabbedPaneChangeListener(final JBTabbedPane tabbedPane)
        {
            this.tabbedPane = tabbedPane;
            this.userActivityLogger = UserActivityLogger.getInstance();
        }

        @Override
        public void stateChanged(ChangeEvent e)
        {
            final int selectedIndex = tabbedPane.getSelectedIndex();
            if (selectedIndex == 0)
            {
                userActivityLogger.log(UserActivityEnum.OverviewMethodTabEntered);
            } else
            {
                userActivityLogger.log(UserActivityEnum.OverviewClassesTabEntered);
            }
        }
    }

    /*
     * UI components related methods
     */

    public void setFilterByThreadPanelVisible(Boolean threadVisualizationsEnabled)
    {
        threadsPanel.setVisible(threadVisualizationsEnabled);
        // TODO: Only for the clex study! Delete following line and uncomment the line above!
        //filterByThreadPanel.setVisible(false);
    }

    /*
     * Artifact pool
     */

    private IArtifactPool artifactPool;

    public void setArtifactPool(final IArtifactPool artifactPool)
    {
        if (artifactPool == null)
        {
            return;
        }
        this.artifactPool = artifactPool;
        applyProgramArtifactVisualizationLabelFactories();
        filterOverView();
        rootPanel.repaint();
    }

    /*
     * Register the artifact visualization label factories used in the tabbed pane
     */

    private Set<AArtifactVisualizationLabelFactory> programArtifactVisualizationLabelFactories;

    public void registerProgramArtifactVisualizationLabelFactories(final AArtifactVisualizationLabelFactory... factories)
    {
        for (final AArtifactVisualizationLabelFactory factory : factories)
        {
            registerProgramArtifactVisualizationLabelFactory(factory);
        }
    }

    public void registerProgramArtifactVisualizationLabelFactory(final AArtifactVisualizationLabelFactory factory)
    {
        if (programArtifactVisualizationLabelFactories == null)
        {
            programArtifactVisualizationLabelFactories = new HashSet<>();
        }
        final boolean noneMatch = programArtifactVisualizationLabelFactories
                .stream()
                .noneMatch(f -> f.getClass().equals(factory.getClass())
                        &&
                        f.getPrimaryMetricIdentifier().equals(factory.getPrimaryMetricIdentifier()
                        ));
        if (noneMatch)
        {
            programArtifactVisualizationLabelFactories.add(factory);
        }
    }

    @SuppressWarnings("unused")
    public boolean removeProgramArtifactVisualizationLabelFactory(final AArtifactVisualizationLabelFactory factory)
    {
        if (programArtifactVisualizationLabelFactories == null)
        {
            return false;
        }
        return programArtifactVisualizationLabelFactories.remove(factory);
    }

    private void applyProgramArtifactVisualizationLabelFactories()
    {
        if (programArtifactVisualizationLabelFactories == null || artifactPool == null)
        {
            return;
        }
        final AArtifact programArtifact = artifactPool.getProgramArtifact();
        if (programArtifact == null)
        {
            return;
        }
        final JPanel wrapper = new JPanel();
//        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.setLayout(new BottomFlowLayout());

        for (final AArtifactVisualizationLabelFactory programArtifactVisualizationLabelFactory :
                programArtifactVisualizationLabelFactories
                        .stream()
                        .sorted(Comparator.comparing(AVisualizationSequence::getSequence))
                        .collect(Collectors.toList()))
        {
            final JLabel artifactLabel = programArtifactVisualizationLabelFactory.createArtifactLabel(programArtifact);
            wrapper.add(artifactLabel);
        }
        programArtifactVisualizationPanel.removeAll();
        programArtifactVisualizationPanel.add(wrapper);
        rootPanel.repaint();
    }


    /*
     * The metric identifier with which the artifact list in the tabs should be sorted.
     */

    private final Object artifactMetricComparatorsForSortingLock = new Object();
    private final Map<Class<? extends AArtifact>, Set<ArtifactMetricComparator>> artifactMetricComparatorsForSorting = new HashMap<>(4);

    public void registerArtifactMetricComparatorForSorting(final Class<? extends AArtifact> artifactClass,
                                                           final ArtifactMetricComparator... artifactMetricComparators)
    {
        if (artifactClass == null || artifactMetricComparators == null)
        {
            return;
        }
        synchronized (artifactMetricComparatorsForSortingLock)
        {
            final Set<ArtifactMetricComparator> comparators = artifactMetricComparatorsForSorting.computeIfAbsent(artifactClass,
                    (ac) -> new HashSet<>(4));
            for (final ArtifactMetricComparator artifactMetricComparator : artifactMetricComparators)
            {
                if (comparators.stream().noneMatch(comp -> comp.getMetricIdentifier().equals(artifactMetricComparator.getMetricIdentifier())))
                {
                    if (artifactMetricComparator.isEnabled())
                    {
                        comparators.forEach(comp -> comp.setEnabled(false));
                    }

                    comparators.add(artifactMetricComparator);
                }
            }
        }
    }

    private Set<ArtifactMetricComparator> getArtifactMetricComparatorsFor(final Class<? extends AArtifact> artifactClass)
    {
        synchronized (artifactMetricComparatorsForSortingLock)
        {
            //noinspection UnnecessaryLocalVariable
            final Set<ArtifactMetricComparator> comparators = artifactMetricComparatorsForSorting.computeIfAbsent(artifactClass,
                    (ac) -> new HashSet<>(4));
            return comparators;
        }
    }

    private ArtifactMetricComparator getAnyArtifactMetricComparator(final Class<? extends AArtifact> artifactClass)
    {
        if (artifactClass == null)
        {
            return null;
        }
        synchronized (artifactMetricComparatorsForSortingLock)
        {
            final Set<ArtifactMetricComparator> artifactMetricComparators = artifactMetricComparatorsForSorting.computeIfAbsent(artifactClass,
                    ac -> new HashSet<>(4));
            final Optional<ArtifactMetricComparator> any = artifactMetricComparators.stream().findAny();
            return any.orElse(null);
        }
    }

    private ArtifactMetricComparator getEnabledArtifactMetricComparator(final Class<? extends AArtifact> artifactClass)
    {
        if (artifactClass == null)
        {
            return null;
        }
        synchronized (artifactMetricComparatorsForSortingLock)
        {
            final Set<ArtifactMetricComparator> artifactMetricComparators = artifactMetricComparatorsForSorting.computeIfAbsent(artifactClass,
                    ac -> new HashSet<>(4));
            final Optional<ArtifactMetricComparator> first = artifactMetricComparators.stream().filter(ArtifactMetricComparator::isEnabled).findFirst();
            return first.orElseGet(() -> getAnyArtifactMetricComparator(artifactClass));
        }
    }

    /*
     *  The artifact visualization label factories which will be used to visualize the artifacts in the lists in the tabbed pane
     */

    private final Object artifactClassVisualizationLabelFactoriesLock = new Object();
    private final Map<Class<? extends AArtifact>, AArtifactVisualizationLabelFactory> artifactClassVisualizationLabelFactories = new HashMap<>(8);

    public void registerArtifactClassVisualizationLabelFactory(final Class<? extends AArtifact> artifactClass, final AArtifactVisualizationLabelFactory factory)
    {
        if (artifactClass == null || factory == null)
        {
            return;
        }
        synchronized (artifactClassVisualizationLabelFactoriesLock)
        {
            artifactClassVisualizationLabelFactories.put(artifactClass, factory);
        }
    }

    public AArtifactVisualizationLabelFactory getArtifactClassVisualizationLabelFactory(final Class<? extends AArtifact> artifactClass)
    {
        if (artifactClass == null)
        {
            return null;
        }
        synchronized (artifactClassVisualizationLabelFactoriesLock)
        {
            return artifactClassVisualizationLabelFactories.get(artifactClass);
        }
    }

    /*
     * That is what happens if any UI interaction takes place!
     */

    public void filterOverView()
    {
        if (artifactPool == null)
        {
            return;
        }
        if (!"".equals(includeFilter.getText()) || !"".equals(excludeFilter.getText()))
        {
            UserActivityLogger.getInstance().log(UserActivityEnum.OverviewArtifactFilterApplied, includeFilter.getText(), excludeFilter.getText());
        }
        ApplicationManager.getApplication().invokeLater(() -> {
            Set<String> includeFilters = retrieveCustomFilters(includeFilter);
            Set<String> excludeFilters = retrieveCustomFilters(excludeFilter);

            final Map<Class<? extends AArtifact>, List<AArtifact>> map = artifactPool.getArtifacts();
            if (map == null)
            {
                return;
            }

            tabbedPane.removeChangeListener(tabbedPaneChangeListener);

            clearTabs();

            for (final Map.Entry<Class<? extends AArtifact>, List<AArtifact>> entry : map.entrySet())
            {
                final Class<? extends AArtifact> artifactClass = entry.getKey();
                List<AArtifact> artifacts = entry.getValue();
//                final ArtifactMetricComparator enabledArtifactMetricComparator = getEnabledArtifactMetricComparator(artifactClass);
//                artifacts = artifacts
//                        .stream()
//                        .filter(aArtifact -> aArtifact.getNumericalMetricValue(enabledArtifactMetricComparator.getMetricIdentifier()) > 0)
//                        .collect(Collectors.toList());
                artifacts = filterArtifacts(artifacts, includeFilters, excludeFilters);

                addTab(artifactClass, artifacts);
            }

            if (lastSelectedTabIndex > 0 && lastSelectedTabIndex < tabbedPane.getTabCount())
            {
                tabbedPane.setSelectedIndex(lastSelectedTabIndex);
            }

            tabbedPane.addChangeListener(tabbedPaneChangeListener);

        });
    }


    private void addTab(final Class<? extends AArtifact> artifactClass, final List<AArtifact> artifacts)
    {
        /*
         * Build the UI
         */

        final JBPanel<BorderLayoutPanel> tabPanel = new BorderLayoutPanel();

        // Need to create these first because they are used in the listener of the combo box for sorting
        final ArtifactOverViewTableModel tableModel = new ArtifactOverViewTableModel(artifacts);
        final MetricTable jbTable = new MetricTable(tableModel)
        {
            @Override
            public String getToolTipText(@NotNull MouseEvent e)
            {
                final Point p = e.getPoint();
                final int rowIndex = rowAtPoint(p);
                final AArtifact artifactAt = tableModel.getArtifactAt(rowIndex);
                if (artifactAt == null)
                {
                    return "";
                }
                String identifier = artifactAt.getIdentifier();
                identifier = identifier.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                return identifier;
            }
        };
        /*
         * Add the sorting panel to the tab panel
         */

        final JBPanel<BorderLayoutPanel> sortArtifactsPanel = new BorderLayoutPanel();
        sortArtifactsPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 2));
        final JBPanel<BorderLayoutPanel> sortArtifactsPanelWrapper = new JBPanel<>();
        sortArtifactsPanelWrapper.setLayout(new BoxLayout(sortArtifactsPanelWrapper, BoxLayout.X_AXIS));

        sortArtifactsPanelWrapper.add(new JBLabel("Sort artifacts by metric: "));
        sortArtifactsPanelWrapper.add(Box.createRigidArea(new Dimension(10, 0)));

        final ComboBox<Comparator<AArtifact>> artifactSortingComboBox = new ComboBox<>();

        final Set<ArtifactMetricComparator> artifactMetricComparators = getArtifactMetricComparatorsFor(artifactClass);
        ArtifactMetricComparator enabledArtifactMetricComparator = null;
        for (final ArtifactMetricComparator artifactMetricComparator : artifactMetricComparators)
        {
            artifactSortingComboBox.addItem(artifactMetricComparator);
            if (artifactMetricComparator.isEnabled())
            {
                artifactSortingComboBox.setSelectedItem(artifactMetricComparator);
                enabledArtifactMetricComparator = artifactMetricComparator;
            }
        }
        if (enabledArtifactMetricComparator == null)
        {
            enabledArtifactMetricComparator = getAnyArtifactMetricComparator(artifactClass);
        }

        tableModel.sortArtifacts(enabledArtifactMetricComparator); // Sort the artifacts in the table

        //noinspection Convert2Lambda
        artifactSortingComboBox.addItemListener(new ItemListener()
        {
            @Override
            public void itemStateChanged(final ItemEvent e)
            {
                final int stateChange = e.getStateChange();
                final ArtifactMetricComparator artifactMetricComparator = (ArtifactMetricComparator) e.getItem();
                if (stateChange == ItemEvent.SELECTED)
                {
                    artifactMetricComparator.setEnabled(true);
                    //noinspection ConstantConditions
                    if (tableModel != null)
                    {
                        tableModel.sortArtifacts(artifactMetricComparator);
                        jbTable.repaint();
                    }
                } else
                {
                    if (stateChange == ItemEvent.DESELECTED)
                    {
                        artifactMetricComparator.setEnabled(false);
                    }
                }
            }
        });

        sortArtifactsPanelWrapper.add(artifactSortingComboBox);
        sortArtifactsPanel.add(sortArtifactsPanelWrapper, BorderLayout.NORTH);

        tabPanel.add(sortArtifactsPanelWrapper, BorderLayout.NORTH);


        /*
         * Build the artifact table and add it to the tab panel
         */

        final MetricTableCellRenderer metricTableCellRenderer = new MetricTableCellRenderer(0);
        jbTable.setDefaultRenderer(Object.class, metricTableCellRenderer);
        jbTable.addMouseMotionListener(new MetricTableMouseMotionAdapter(jbTable));
        jbTable.addMouseListener(new ArtifactOverviewTableMouseAdapter(jbTable));
        jbTable.setExpandableItemsEnabled(false);

        int minWidth = 100;
        int maxWidth = 150;

        final TableColumn visColumn = jbTable.getColumnModel().getColumn(0);
        visColumn.setMinWidth(minWidth);
        visColumn.setPreferredWidth(maxWidth);
        visColumn.setMaxWidth(maxWidth);

        jbTable.setRowHeight(jbTable.getRowHeight() + 5);
        tabPanel.add(new JBScrollPane(jbTable), BorderLayout.CENTER);

        /*
         *
         */

        final String tabName = artifactPool.getArtifactClassDisplayName(artifactClass);
        tabbedPane.addTab(tabName, tabPanel);
    }

    private Set<String> retrieveCustomFilters(final JBTextField filter)
    {
        final String str = filter.getText().trim();
        final String[] split = str.split(",");
        final Set<String> strings = new HashSet<>();
        for (String s : split)
        {
            s = s.trim();
            if (!s.isEmpty())
            {
                strings.add(s);
            }
        }
        return strings;
    }

    private ICurrentFileArtifactFilter currentFileArtifactFilter;

    public void registerCurrentFileArtifactFilter(final ICurrentFileArtifactFilter currentFileArtifactFilter)
    {
        this.currentFileArtifactFilter = currentFileArtifactFilter;
        if (this.currentFileArtifactFilter != null)
        {
            this.currentFileFilter.setEnabled(true);
        }
    }

    private IArtifactFilter standardLibraryArtifactFilter;

    public void registerStandardLibraryArtifactFilter(final IArtifactFilter standardLibraryArtifactFilter)
    {
        this.standardLibraryArtifactFilter = standardLibraryArtifactFilter;
        if (this.standardLibraryArtifactFilter != null)
        {
            this.standardLibraryFilter.setEnabled(true);
        }
    }

    private AThreadStateArtifactFilter threadStateArtifactFilter;

    public void registerThreadStateArtifactFilter(final AThreadStateArtifactFilter threadStateArtifactFilter)
    {
        // This method is currently disabled because the feature of differentiating between the runtime components: running, blocked, waiting, sleeping is
        // not yet implemented!

//        if (threadStateFilterWrapper == null)
//        {
//            return;
//        }
//        if (threadStateFilterWrapper.getComponentCount() > 0)
//        { // Check if the threadStateFilterWrapper has already been set up. Necessary when profiling is started multiple times.
//            return;
//        }
//        this.threadStateArtifactFilter = threadStateArtifactFilter;
//        final Collection<String> stateStrings = threadStateArtifactFilter.getStateStrings();
//        final int rows = (int) Math.ceil(stateStrings.size() / 2d);
//        JPanel threadStatesGrid = new JPanel(new GridLayout(rows, 2));
//        ItemListener itemListener = e -> filterOverView();
//        for (String stateString : stateStrings)
//        {
//            JBCheckBox cb = new JBCheckBox(stateString);
//            cb.setSelected(true);
//            threadStatesGrid.add(cb);
//            cb.addItemListener(itemListener);
//        }
//        threadStateFilterWrapper.add(threadStatesGrid, BorderLayout.CENTER);
    }

    private List<AArtifact> filterArtifacts(
            final Collection<? extends AArtifact> artifacts
            , final Set<String> includeElements
            , final Set<String> excludeElements
    )
    {
        if (artifacts == null || artifacts.isEmpty())
        {
            return new ArrayList<>();
        }
        final Set<AArtifact> filtered = new HashSet<>();

        /*
         * Include filters.
         */

        final boolean currentFileFilterSelected = currentFileFilter.isSelected();
        if (currentFileFilterSelected)
        {
            if (currentFileArtifactFilter == null)
            {
                CodeSparksLogger.addText(String.format("%s: current file artifact filter not setup!", getClass()));
            } else
            {
                final Project project = CoreUtil.getCurrentlyOpenedProject();
                final EditorEx selectedFileEditor = CoreUtil.getSelectedFileEditor(project);
                if (selectedFileEditor != null)
                {
                    final VirtualFile virtualFile = selectedFileEditor.getVirtualFile();
                    final PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
                        PsiManager psiManager = PsiManager.getInstance(project);
                        return psiManager.findFile(virtualFile);
                    });

                    filtered.addAll(currentFileArtifactFilter.filterArtifact(artifacts, psiFile));
                }
            }
        }

        if (includeElements == null || includeElements.isEmpty())
        {
            if (!currentFileFilterSelected)
            {
                filtered.addAll(artifacts);
            }
        } else
        {
            filtered.addAll(artifacts.stream()
                    .filter(artifact -> includeElements.stream()
                            .anyMatch(s -> artifact.getIdentifier().toLowerCase().contains(s.toLowerCase())))
                    .collect(Collectors.toSet()));
        }

        /*
         * Exclude filters.
         */
        final boolean notPureThreadArtifacts = artifacts.stream().anyMatch(artifact -> !AThreadArtifact.class.isAssignableFrom(artifact.getClass()));
        if (notPureThreadArtifacts)
        { // Thread artifacts should not be affected by this filter because often many threads are not modeled as dedicated subclasses of java.lang.Thread
            // but rather java.lang.Threads which were passed a lambda or Runnable object as parameter.
            if (standardLibraryFilter.isSelected())
            {
                if (standardLibraryArtifactFilter == null)
                {
                    CodeSparksLogger.addText(String.format("%s: standard library artifact filter not setup!", getClass()));
                } else
                {
                    filtered.removeAll(filtered.stream()
                            .filter(standardLibraryArtifactFilter::filterArtifact)
                            .collect(Collectors.toSet()));
                }
            }
        }
        if (excludeElements != null && !excludeElements.isEmpty())
        {
            // Then remove all elements to exclude
            filtered.removeAll(filtered.stream()
                    .filter(artifact -> excludeElements.stream()
                            .anyMatch(s -> artifact.getIdentifier().toLowerCase().contains(s.toLowerCase())))
                    .collect(Collectors.toSet()));
        }

        /*
         * Thread state filter
         */
        if (threadStateArtifactFilter != null)
        {
            filtered.retainAll(threadStateArtifactFilter.filterArtifact(filtered));
        }

        /*
         * Alternatively, keep all artifacts which have at least one thread which is not filtered.
         */
        final Set<AArtifact> nonThreadFilterArtifacts = filtered.stream()
                .filter(artifact -> artifact.hasThreads() &&
                        artifact.getThreadArtifacts().stream().anyMatch(AThreadArtifact::isSelected))
                .collect(Collectors.toSet());
        filtered.retainAll(nonThreadFilterArtifacts);

        return new ArrayList<>(filtered);
    }

    private int lastSelectedTabIndex = -1;

    private void clearTabs()
    {
        if (tabbedPane != null)
        {
            lastSelectedTabIndex = tabbedPane.getSelectedIndex();
            tabbedPane.removeAll();
        }
    }
}
