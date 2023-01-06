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

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Oliver Moseler on 16.10.2014.
 */
public class MetricTableCellRenderer extends JTextArea implements TableCellRenderer
{
    private static Font defaultFont = null;
    private static Font underlinedFont = null;
    private static final Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private final Set<Integer> excludeColumns;
//    private static Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);

    private static void createFonts(JTextArea area)
    {
        if (defaultFont != null && underlinedFont != null)
        {
            return;
        }
        defaultFont = area.getFont();
        Map<TextAttribute, Object> map = new HashMap<>();
        map.put(TextAttribute.FONT, defaultFont);
        map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        underlinedFont = Font.getFont(map);
    }

    public MetricTableCellRenderer()
    {
        this.excludeColumns = new HashSet<>();
        init();
    }

    public MetricTableCellRenderer(int... columnsToExclude)
    {
        this.excludeColumns = new HashSet<>();
        for (int i : columnsToExclude)
        {
            excludeColumns.add(i);
        }
        init();

    }

    private void init()
    {
        setDoubleBuffered(true);
        MetricTableCellRenderer.createFonts(this);
    }

    @Override
    public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row,
                                                   final int column)
    {
        final MetricTable metricTable = (MetricTable) table;
        setBackground(null); // Reset the hover background color. See JBTable hover background
        if (row != metricTable.getEnteredRow() || column != metricTable.getEnteredCol())
        {
            if (value instanceof JLabel)
            {
                final JLabel jLabel = (JLabel) value;
                jLabel.setBackground(null); // Reset the hover background color. See JBTable hover background
                jLabel.setHorizontalAlignment(JLabel.CENTER);
                return jLabel;
            }
            setFont(defaultFont);
            if (value == null)
            {
                setText("");
            } else
            {
                final String text = value.toString();
                if (!getText().equals(text))
                {
                    setText(text);
                }
            }
            return this;
        }

        if (excludeColumns.contains(column))
        {
            if (!table.getCursor().equals(Cursor.getDefaultCursor()))
            {
                table.setCursor(Cursor.getDefaultCursor());
            }
            if (value instanceof JLabel)
            {
                final JLabel jLabel = (JLabel) value;
                jLabel.setBackground(null); // Reset the hover background color. See JBTable hover background
                jLabel.setHorizontalAlignment(JLabel.CENTER);
                return jLabel;
            }
            setFont(defaultFont);
            setText("");
        } else
        {
            if (value == null)
            {
                setText("");
                table.setCursor(Cursor.getDefaultCursor());
                setFont(defaultFont);
            } else
            {
                String text = value.toString();
                if (!getText().equals(text))
                {
                    setText(text);
                }
                if (getText() == null || "".equals(getText()))
                {
                    table.setCursor(Cursor.getDefaultCursor());
                    setFont(defaultFont);
                } else
                {
                    if (!getCursor().equals(handCursor))
                    {
                        table.setCursor(handCursor);
                    }
                    if (!getFont().equals(underlinedFont))
                    {
                        setFont(underlinedFont);
                    }
                }
            }
        }
        return this;
    }
}
