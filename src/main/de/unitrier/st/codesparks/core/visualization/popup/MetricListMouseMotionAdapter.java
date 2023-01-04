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
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.components.JBTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

public class MetricListMouseMotionAdapter extends MouseMotionAdapter
{
    private final MetricList list;
    private static Font underlinedFont = null;

    public MetricListMouseMotionAdapter(MetricList list)
    {
        this.list = list;
        Map<TextAttribute, Object> map = new HashMap<>();
        map.put(TextAttribute.FONT, new JBTextArea().getFont());
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        underlinedFont = Font.getFont(map);
    }

    private static final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

    @Override
    public void mouseMoved(MouseEvent e)
    {
        Point point = e.getPoint();
        int i = list.locationToIndex(point);
        NumericalMetricListModel model = (NumericalMetricListModel) list.getModel();
        model.resetFont();
        JTextArea elementAt = model.getElementAt(i);
        elementAt.setFont(underlinedFont);
        list.updateUI();
        list.setCursor(handCursor);
    }
}
