package de.unitrier.st.codesparks.core.data;

import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

class Lazy<T>
{
    private T value;

    private Supplier<T> supplier;

    Lazy(Supplier<T> supplier)
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