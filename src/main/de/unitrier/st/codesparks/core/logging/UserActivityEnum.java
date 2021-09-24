/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.logging;

public enum UserActivityEnum
{
    ProfilingStarted,
    ProfilingEnded,
    SampleAnalysisTriggered,
    CalleeTooltipClicked,
    CalleeTooltipNavigated,
    MethodPopupCallersTabEntered,
    MethodPopupCalleesTabEntered,
    ThreadClusterHovered,
    ThreadClusterToggleButtonClicked,
    ThreadRadarPopupTypesTabEntered,
    ThreadRadarPopupClustersTabEntered,
    ThreadFilterApplied,
    OverviewThreadFilterReset,
    MethodPopupNavigatedToClass,
    MethodPopupNavigatedToMethod,
    OverviewOpened,

    OverviewSwitchedToArtifactTab,

    OverviewArtifactIncludeFiltered,
    OverviewArtifactExcludeFiltered,
    OverviewArtifactFilterReset,
    OverviewArtifactFilterApplied,
    OverviewArtifactFilterCurrentEditorArtifactsOnlyChecked,
    OverviewArtifactFilterExcludeStandardLibraryChecked,
    OverviewNavigated,
    OverviewArtifactsSorted,

    MethodPopupCalleesTabSelected,
    MethodPopupCallersTabSelected,
    ThreadTreeNodeToggled,
    // ThreadRadar
    ThreadRadarDetailViewSelectAllButtonClicked,
    ThreadRadarDetailViewDeselectAllButtonClicked,
    ThreadRadarDetailViewInvertSelectionButtonClicked,
    ThreadRadarDetailViewResetThreadFilterButtonClicked,
    ThreadRadarDetailViewApplyThreadFilterButtonClicked,
    // ThreadFork
    ThreadForkDetailViewOpened,
    ThreadForkDetailViewNumberOfClustersSelected,

    ThreadForkDetailViewSwitchedToVisualizationTab,

    ThreadForkDetailViewSwitchedToSelectablesTab,

    ThreadForkDetailViewSelectAllButtonClicked,
    ThreadForkDetailViewDeselectAllButtonClicked,
    ThreadForkDetailViewInvertSelectionButtonClicked,
    ThreadForkDetailViewResetThreadFilterButtonClicked,
    ThreadForkDetailViewApplyThreadFilterButtonClicked,

    ThreadForkClusterButtonHovered,
    ThreadForkDetailViewClusterBarClicked,

    HistogramShowDensityFunction,

    //
    PopupPinned,
    PopupOpened,
    PopupNavigated,

    FileSelected,
    ApplicationClosed
}
