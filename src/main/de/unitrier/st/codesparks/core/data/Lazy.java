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
