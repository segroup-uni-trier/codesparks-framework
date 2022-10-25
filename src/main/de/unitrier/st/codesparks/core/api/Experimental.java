/*
 * Copyright (c) 2022. Oliver Moseler
 */

package de.unitrier.st.codesparks.core.api;

import java.lang.annotation.*;

/**
 * This element has an experimental maturity.  Use with caution.
 * <p>
 * NOTE: The developers of this element is not responsible for the issues created,
 * using it is not suggested for production environment. If you see this annotation do this, do not do that etc.
 * Enjoy responsibly....
 */


@Documented //this annotation maybe helpful for your custom annotation
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.CONSTRUCTOR, ElementType.LOCAL_VARIABLE, ElementType.PACKAGE,
        ElementType.ANNOTATION_TYPE, ElementType.TYPE_USE, ElementType.TYPE_PARAMETER
})

// See https://stackoverflow.com/questions/55402614/is-there-a-way-to-declare-a-class-method-as-experimental-in-java, as of October 25, 2022
public @interface Experimental {}
