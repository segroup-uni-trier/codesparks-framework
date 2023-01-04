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
package de.unitrier.st.codesparks.core.localization;

import de.unitrier.st.codesparks.core.logging.CodeSparksLogger;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public final class LocalizationUtil
{
    private LocalizationUtil() {}

    public static String getLocalizedString(String key)
    {
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("codesparks-strings", Locale.getDefault());
        String string;
        try
        {
            string = resourceBundle.getString(key);
        } catch (MissingResourceException e)
        {
            CodeSparksLogger.addText(e.getMessage());
            return "NA";
        }
        return string;
    }
}
