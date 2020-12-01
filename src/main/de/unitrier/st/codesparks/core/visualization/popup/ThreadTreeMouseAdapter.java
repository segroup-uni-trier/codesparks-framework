//package de.unitrier.st.codesparks.core.visualization.popup;
//
//import com.intellij.openapi.application.ApplicationManager;
//import de.unitrier.st.codesparks.core.visualization.thread.AThreadRadar;
//
//import javax.swing.*;
//import javax.swing.tree.TreePath;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
///*
// * Copyright (c), Oliver Moseler, 2020
// */
//public class ThreadTreeMouseAdapter extends MouseAdapter
//{
//    private final AThreadRadar threadRadar;
//
//    public ThreadTreeMouseAdapter(AThreadRadar threadRadar)
//    {
//        this.threadRadar = threadRadar;
//    }
//
//    @Override
//    public void mouseClicked(MouseEvent e)
//    {
//        final JTree tree = (JTree) e.getSource();
//        if (tree == null)
//        {
//            return;
//        }
//        final java.awt.Point point = e.getPoint();
//        final TreePath pathForLocation = tree.getPathForLocation(point.x, point.y);
//        if (pathForLocation == null)
//        {
//            return;
//        }
//        ColoredSelectableTreeNode lastPathComponent = (ColoredSelectableTreeNode) pathForLocation.getLastPathComponent();
//        if (lastPathComponent == null)
//        {
//            return;
//        }
//        lastPathComponent.toggleSelected();
//        ApplicationManager.getApplication().invokeLater(tree::updateUI);
//        threadRadar.repaint();
//    }
//}
