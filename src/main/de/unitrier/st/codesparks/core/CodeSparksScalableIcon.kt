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

package de.unitrier.st.codesparks.core

import com.intellij.openapi.util.ScalableIcon
import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon
import javax.swing.ImageIcon

class CodeSparksScalableIcon(private val imageIcon: ImageIcon) : ScalableIcon {

    override fun paintIcon(c: Component?, g: Graphics?, x: Int, y: Int) = imageIcon.paintIcon(c, g, x, y)

    override fun getIconWidth(): Int = imageIcon.iconWidth

    override fun getIconHeight(): Int = imageIcon.iconHeight

    override fun getScale(): Float = 1.0f

    override fun scale(p0: Float): Icon = imageIcon
}