package de.unitrier.st.codesparks.core.overview;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.*;
import com.intellij.util.ui.components.BorderLayoutPanel;
import de.unitrier.st.codesparks.core.data.AArtifact;
import de.unitrier.st.codesparks.core.data.GlobalResetThreadFilter;
import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;
import de.unitrier.st.codesparks.core.logging.UserActivityEnum;
import de.unitrier.st.codesparks.core.logging.UserActivityLogger;
import de.unitrier.st.codesparks.core.CoreUtil;
import de.unitrier.st.codesparks.core.IArtifactPool;
import de.unitrier.st.codesparks.core.CodeSparksFlowManager;
import de.unitrier.st.codesparks.core.data.ArtifactMetricValueSelfComparator;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import de.unitrier.st.codesparks.core.logging.IUserActivityLogger;
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
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


public class ArtifactOverview
{
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

    public void setFilterByThreadPanelVisible(Boolean threadVisualizationsEnabled)
    {
        filterByThreadPanel.setVisible(threadVisualizationsEnabled);
    }

    private IArtifactPool artifactPool;

    public void setProfilingResult(final IArtifactPool artifactPool)
    {
        if (artifactPool == null)
        {
            return;
        }
        this.artifactPool = artifactPool;
        filterOverView();
        rootPanel.repaint();
    }

    IArtifactPool getProfilingResult()
    {
        return this.artifactPool;
    }

    private ArtifactOverview()
    {
        setupUI();
    }

    private void setupUI()
    {
        ProgramThreadRadar programThreadRadar = new ProgramThreadRadar(this);
        rootPanel = new BorderLayoutPanel();//new JBPanel();
//        rootPanel.setPreferredSize(new Dimension(300, 500));
//        rootPanel.setMaximumSize(new Dimension(300, 500));
//        rootPanel.setLayout(new BorderLayout());
        JBPanel<BorderLayoutPanel> filterPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());

        JBPanel<BorderLayoutPanel> filterPanelWrapper = new JBPanel<>();
        filterPanelWrapper.setLayout(new BoxLayout(filterPanelWrapper, BoxLayout.Y_AXIS));

        JBPanel<BorderLayoutPanel> filterByIdentifierPanel = new JBPanel<>();
        filterByIdentifierPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Filter artifacts by identifier (separate with comma)"));

        filterByIdentifierPanel.setLayout(new BoxLayout(filterByIdentifierPanel, BoxLayout.Y_AXIS));

        excludeFilter = new JBTextField();
        JBPanel<BorderLayoutPanel> excludeFilterPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
        excludeFilterPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JBPanel<BorderLayoutPanel> excludeFilterPanelWrapper = new JBPanel<>();
        excludeFilterPanelWrapper.setLayout(new BoxLayout(excludeFilterPanelWrapper, BoxLayout.X_AXIS));
        excludeFilterPanelWrapper.add(new JBLabel("Exclude: "));
        excludeFilterPanelWrapper.add(excludeFilter);
        excludeFilterPanel.add(excludeFilterPanelWrapper, BorderLayout.CENTER);
        filterByIdentifierPanel.add(excludeFilterPanel);

        includeFilter = new JBTextField();
        JBPanel<BorderLayoutPanel> includeFilterPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
        includeFilterPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JBPanel<BorderLayoutPanel> includeFilterPanelWrapper = new JBPanel<>();
        includeFilterPanelWrapper.setLayout(new BoxLayout(includeFilterPanelWrapper, BoxLayout.X_AXIS));
        includeFilterPanelWrapper.add(new JBLabel("Include: "));
        includeFilterPanelWrapper.add(Box.createRigidArea(new Dimension(2, 0)));
        includeFilterPanelWrapper.add(includeFilter);
        includeFilterPanel.add(includeFilterPanelWrapper, BorderLayout.CENTER);
        filterByIdentifierPanel.add(includeFilterPanel);


        JBPanel<BorderLayoutPanel> filterArtifactButtonsPanelWrapper = new JBPanel<>();
        filterArtifactButtonsPanelWrapper.setLayout(new BoxLayout(filterArtifactButtonsPanelWrapper, BoxLayout.X_AXIS));

        JBPanel<BorderLayoutPanel> applyArtifactFilterButtonPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
        applyArtifactFilterButtonPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 2));
        JButton applyArtifactFilterButton = new JButton(
                LocalizationUtil.getLocalizedString("codesparks.ui.overview.button.apply.artifact.filter"));
        applyArtifactFilterButtonPanel.add(applyArtifactFilterButton, BorderLayout.CENTER);

        JBPanel<BorderLayoutPanel> resetArtifactFilterPanel = new BorderLayoutPanel();//new JBPanel(new BorderLayout());
        resetArtifactFilterPanel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 2));
        JButton resetArtifactFilterButton = new JButton(
                LocalizationUtil.getLocalizedString("codesparks.ui.overview.button.apply.artifact.filter.reset"));
        resetArtifactFilterPanel.add(resetArtifactFilterButton, BorderLayout.CENTER);

        filterArtifactButtonsPanelWrapper.add(resetArtifactFilterPanel);
        filterArtifactButtonsPanelWrapper.add(applyArtifactFilterButtonPanel);

        filterByIdentifierPanel.add(filterArtifactButtonsPanelWrapper/*, FlowLayout.TRAILING*/);

        /*
         * Filter checkboxes
         */
        JBPanel<BorderLayoutPanel> filterCheckboxesPanel = new BorderLayoutPanel();// new JBPanel(new BorderLayout());
        filterCheckboxesPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 2));
        JBPanel<BorderLayoutPanel> filterCheckboxesPanelWrapper = new JBPanel<>();
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

//        final Boolean threadVisualizationsEnabled = PropertiesUtil.getBooleanPropertyValueOrDefault(PropertiesFile.USER_INTERFACE_PROPERTIES,
//                PropertyKey.THREAD_VISUALIZATIONS_ENABLED, true);
//
//        if (threadVisualizationsEnabled)
//        {
        filterByThreadPanel = new BorderLayoutPanel();// new JBPanel(new BorderLayout());
        filterByThreadPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Filter artifacts by thread"));

        JBPanel<BorderLayoutPanel> threadFilterWrapper = new JBPanel<>();
        threadFilterWrapper.setLayout(new BoxLayout(threadFilterWrapper, BoxLayout.X_AXIS));
        threadFilterWrapper.add(programThreadRadar);

        final JButton resetThreadFilterButton = new JButton(
                LocalizationUtil.getLocalizedString("codesparks.ui.button.reset.thread.filter.global"));
        resetThreadFilterButton.addActionListener(e -> {

            UserActivityLogger.getInstance().log(UserActivityEnum.OverviewThreadFilterReset);

            CodeSparksFlowManager.getInstance().getCurrentCodeSparksFlow().applyThreadArtifactFilter(GlobalResetThreadFilter
                    .getInstance());
        });
        JBPanel<BorderLayoutPanel> resetThreadFilterButtonWrapper = new BorderLayoutPanel();// new JBPanel(new BorderLayout());
        resetThreadFilterButtonWrapper.add(resetThreadFilterButton, BorderLayout.CENTER);
        resetThreadFilterButtonWrapper.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        threadFilterWrapper.add(resetThreadFilterButtonWrapper);
        if (threadStateFilterWrapper == null)
        {
            threadStateFilterWrapper = new BorderLayoutPanel();//new JBPanel<>();
        }
        threadFilterWrapper.add(threadStateFilterWrapper);
        filterByThreadPanel.add(threadFilterWrapper, BorderLayout.CENTER);

        filterPanelWrapper.add(filterByThreadPanel);
//        }

        filterPanel.add(filterPanelWrapper, BorderLayout.CENTER);

        rootPanel.add(filterPanel, BorderLayout.NORTH);
        JBPanel<BorderLayoutPanel> resultsPanel = new BorderLayoutPanel();// new JBPanel(new BorderLayout());
        tabbedPane = new JBTabbedPane();
        tabbedPaneChangeListener = new ArtifactTabbedPaneChangeListener(tabbedPane);

        resultsPanel.add(tabbedPane, BorderLayout.CENTER);
        rootPanel.add(resultsPanel, BorderLayout.CENTER);

        ActionListener actionListener = e -> filterOverView();


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

    private JCheckBox currentFileFilter;
    private JCheckBox standardLibraryFilter;
    private JBPanel<BorderLayoutPanel> rootPanel;
    private JBPanel<BorderLayoutPanel> filterByThreadPanel;
    private JBTabbedPane tabbedPane;

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

    private ChangeListener tabbedPaneChangeListener;

    private JBTextField excludeFilter;
    private JBTextField includeFilter;

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
            Map<String, List<AArtifact>> lists = artifactPool.getNamedArtifactTypeLists();

            if (lists == null)
            {
                return;
            }

            ArtifactMetricValueSelfComparator artifactMetricValueSelfComparator =
                    new ArtifactMetricValueSelfComparator();

            tabbedPane.removeChangeListener(tabbedPaneChangeListener);

            clear();

            for (Map.Entry<String, List<AArtifact>> entry : lists.entrySet())
            {
                List<AArtifact> artifacts = entry.getValue();
                artifacts = filterArtifacts(artifacts, includeFilters, excludeFilters);
                artifacts.sort(artifactMetricValueSelfComparator);
                String tabName = entry.getKey();
                addTab(tabName, artifacts);
            }

            if (lastSelectedIndex > 0 && lastSelectedIndex < tabbedPane.getTabCount())
            {
                tabbedPane.setSelectedIndex(lastSelectedIndex);
//                if (lastSelectedIndex == 0)
//                    UserActivityLogger.getInstance().log(UserActivityEnum.OverviewMethodTabEntered);
//                else
//                    UserActivityLogger.getInstance().log(UserActivityEnum.OverviewClassesTabEntered);
            }

            tabbedPane.addChangeListener(tabbedPaneChangeListener);

        });
    }

    private void addTab(String title, List<AArtifact> artifacts)
    {
        final ArtifactOverViewTableModel tableModel = new ArtifactOverViewTableModel(artifacts);
        final MetricTable jbTable = new MetricTable(tableModel)
        {
            @Override
            public String getToolTipText(@NotNull MouseEvent e)
            {
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                AArtifact artifactAt = tableModel.getArtifactAt(rowIndex);
                if (artifactAt == null)
                {
                    return "";
                }
                String identifier = artifactAt.getIdentifier();
                identifier = identifier.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
                return identifier;
            }
        };
        jbTable.setDefaultRenderer(Object.class, new MetricTableCellRenderer(0));
        jbTable.addMouseMotionListener(new MetricTableMouseMotionAdapter(jbTable));
        jbTable.addMouseListener(new ArtifactOverviewTableMouseAdapter(jbTable));
        jbTable.setExpandableItemsEnabled(false);

        int minWidth = 100;
        int maxWidth = 150;

        TableColumn visColumn = jbTable.getColumnModel().getColumn(0);
        visColumn.setMinWidth(minWidth);
        visColumn.setPreferredWidth(maxWidth);
        visColumn.setMaxWidth(maxWidth);

        jbTable.setRowHeight(jbTable.getRowHeight() + 5);

        tabbedPane.addTab(title, new JBScrollPane(jbTable));
    }

    public JPanel getRootPanel()
    {
        return this.rootPanel;
    }

    private Set<String> retrieveCustomFilters(JBTextField filter)
    {
        String str = filter.getText().trim();
        String[] split = str.split(",");
        Set<String> strings = new HashSet<>();
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

    public void registerCurrentFileArtifactFilter(ICurrentFileArtifactFilter currentFileArtifactFilter)
    {
        this.currentFileArtifactFilter = currentFileArtifactFilter;
        if (this.currentFileArtifactFilter != null)
        {
            this.currentFileFilter.setEnabled(true);
        }
    }

    private IArtifactFilter standardLibraryArtifactFilter;

    public void registerStandardLibraryArtifactFilter(IArtifactFilter standardLibraryArtifactFilter)
    {
        this.standardLibraryArtifactFilter = standardLibraryArtifactFilter;
        if (this.standardLibraryArtifactFilter != null)
        {
            this.standardLibraryFilter.setEnabled(true);
        }
    }

    private AThreadStateArtifactFilter threadStateArtifactFilter;
    private JBPanel<BorderLayoutPanel> threadStateFilterWrapper;

    public void registerThreadStateArtifactFilter(AThreadStateArtifactFilter threadStateArtifactFilter)
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

    private List<AArtifact> filterArtifacts(Collection<? extends AArtifact> artifacts, Set<String> includeElements,
                                            Set<String> excludeElements)
    {
        List<AArtifact> filtered = new ArrayList<>();
        if (artifacts == null)
        {
            return filtered;
        }

        /*
         * Include filters.
         */

        boolean currentFileFilterSelected = currentFileFilter.isSelected();
        if (currentFileFilterSelected)
        {
            if (currentFileArtifactFilter == null)
            {
                CodeSparksLogger.addText(String.format("%s: current file artifact filter not setup!", getClass()));
            } else
            {
                Project project = CoreUtil.getCurrentlyOpenedProject();
                EditorEx selectedFileEditor = CoreUtil.getSelectedFileEditor(project);
                if (selectedFileEditor != null)
                {
                    VirtualFile virtualFile = selectedFileEditor.getVirtualFile();
                    PsiFile psiFile = ApplicationManager.getApplication().runReadAction((Computable<PsiFile>) () -> {
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
                    .collect(Collectors.toList()));
        }

        /*
         * Exclude filters.
         */

        if (standardLibraryFilter.isSelected())
        {
            if (standardLibraryArtifactFilter == null)
            {
                CodeSparksLogger.addText(String.format("%s: standard library artifact filter not setup!", getClass()));
            } else
            {
                filtered.removeAll(filtered.stream()
                        .filter(standardLibraryArtifactFilter::filterArtifact)
                        .collect(Collectors.toList()));
            }
        }
        if (excludeElements != null && !excludeElements.isEmpty())
        {
            // Then remove all elements to exclude
            filtered.removeAll(filtered.stream()
                    .filter(artifact -> excludeElements.stream()
                            .anyMatch(s -> artifact.getIdentifier().toLowerCase().contains(s.toLowerCase())))
                    .collect(Collectors.toList()));
        }

        /*
         * State filter
         */
        if (threadStateArtifactFilter != null)
        {
            filtered.retainAll(threadStateArtifactFilter.filterArtifact(filtered));
        }

        return filtered;
    }

    private int lastSelectedIndex = -1;

    private void clear()
    {
        if (tabbedPane != null)
        {
            lastSelectedIndex = tabbedPane.getSelectedIndex();
            tabbedPane.removeAll();
        }
    }
}
