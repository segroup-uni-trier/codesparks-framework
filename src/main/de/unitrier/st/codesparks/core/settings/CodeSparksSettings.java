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

/*
 * Copyright (c), Oliver Moseler, 2020
 */
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
