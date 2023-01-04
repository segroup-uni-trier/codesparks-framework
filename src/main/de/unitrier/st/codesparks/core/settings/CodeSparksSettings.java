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
package de.unitrier.st.codesparks.core.settings;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.options.UnnamedConfigurable;
import de.unitrier.st.codesparks.core.localization.LocalizationUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

@Service
public final class CodeSparksSettings implements SearchableConfigurable.Parent
{
    private final List<Configurable> children;

    public CodeSparksSettings()
    {
        children = new ArrayList<>();
    }

    private static final String CODESPARKS_SETTINGS_ID = "Codesparks-Settings-ID";

    @NotNull
    @Override
    public String getId()
    {
        return CODESPARKS_SETTINGS_ID;
    }

    @Override
    public String getDisplayName()
    {
        return LocalizationUtil.getLocalizedString("codesparks.settings.displayname");
    }

    @NotNull
    @Override
    public Configurable[] getConfigurables()
    {
        return children.toArray(new Configurable[0]);
    }

    @Nullable
    @Override
    public JComponent createComponent()
    {
        return null;
    }

    @Override
    public boolean isModified()
    {
        return children.stream().anyMatch(UnnamedConfigurable::isModified);
    }

    @Override
    public void apply() //throws ConfigurationException
    {

    }

    /**
     * This is the service method.
     *
     * @param configurable The configurable to register.
     */
    public void registerConfigurable(Configurable configurable)
    {
        children.add(configurable);
    }
}
