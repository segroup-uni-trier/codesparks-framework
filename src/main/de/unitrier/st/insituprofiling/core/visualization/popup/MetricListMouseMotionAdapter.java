package de.unitrier.st.insituprofiling.core.visualization.popup;

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
        MetricListModel model = (MetricListModel) list.getModel();
        model.resetFont();
        JTextArea elementAt = model.getElementAt(i);
        elementAt.setFont(underlinedFont);
        list.updateUI();
        list.setCursor(handCursor);
    }
}
