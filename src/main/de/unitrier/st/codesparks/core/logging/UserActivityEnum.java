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
