package de.unitrier.st.insituprofiling.core.logging;

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
    OverviewClassesTabEntered,
    OverviewMethodTabEntered,
    OverviewArtifactIncludeFiltered,
    OverviewArtifactExcludeFiltered,
    OverviewArtifactFilterReset,
    OverviewArtifactFilterApplied,
    OverviewArtifactFilterCurrentEditorArtifactsOnlyChecked,
    OverviewArtifactFilterExcludeStandardLibraryChecked,
    MethodPopupCalleesTabSelected,
    MethodPopupCallersTabSelected,
    ThreadTreeNodeToggled,
    ThreadRadarDetailsViewSelectAllButtonClicked, ThreadRadarDetailsViewDeselectAllButtonClicked, ThreadRadarDetailsViewInvertSelectionButtonClicked, ThreadRadarDetailsViewResetThreadFilterButtonClicked, ThreadRadarDetailsViewApplyThreadFilterButtonClicked, PopupPinned, PopupOpened, PopupNavigated, OverviewNavigated, FileSelected, ApplicationClosed
}
