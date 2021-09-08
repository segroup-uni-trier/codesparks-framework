/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.data;

import java.io.Serializable;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

class Lazy<T> implements Serializable
{
    private volatile T value;

    private Supplier<T> supplier;

    Lazy(final Supplier<T> supplier)
    {
        this.supplier = supplier;
    }

    Lazy() {}

    T getOrCompute(final Supplier<T> supplier)
    {
        if (supplier == null)
        {
            return null;
        }
        if (value == null)
        {
            synchronized (this)
            {
                if (value == null)
                {
                    value = requireNonNull(supplier.get());
                }
            }
        }
        return value;
    }

    T getOrCompute()
    {
        return getOrCompute(this.supplier);
    }

    T get()
    {
        synchronized (this)
        {
            return value;
        }
    }
}
