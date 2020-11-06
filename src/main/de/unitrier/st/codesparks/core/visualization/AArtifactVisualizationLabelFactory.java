package de.unitrier.st.codesparks.core.visualization;

/*
 * Copyright (C) 2020, Oliver Moseler
 */
public abstract class AArtifactVisualizationLabelFactory extends AVisualizationSequence
        implements IArtifactVisualizationLabelFactory
{
    protected AArtifactVisualizationLabelFactory() {}

    protected AArtifactVisualizationLabelFactory(int sequence) {super(sequence, false);}

    protected AArtifactVisualizationLabelFactory(int sequence, boolean isDefault) {super(sequence, isDefault);}
}