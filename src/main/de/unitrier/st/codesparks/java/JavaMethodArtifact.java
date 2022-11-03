/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.java;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Oliver Moseler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JavaMethodArtifact
{
}
