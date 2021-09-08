/*
 * Copyright (c) 2021. Oliver Moseler
 */
package de.unitrier.st.codesparks.core.visualization.popup;

import com.intellij.ui.table.JBTable;

import javax.swing.table.TableModel;

/**
 * Created by Oliver Moseler on 16.10.2014.
 */
public class MetricTable extends JBTable
{
    private int enteredRow;
    private int enteredCol;

    protected MetricTable(TableModel model)
    {
        super(model);
        enteredCol = -1;
        enteredRow = -1;
    }

    int getEnteredRow()
    {
        return enteredRow;
    }

    void setEnteredRow(int enteredRow)
    {
        this.enteredRow = enteredRow;
    }

    int getEnteredCol()
    {
        return enteredCol;
    }

    void setEnteredCol(int enteredCol)
    {
        this.enteredCol = enteredCol;
    }
}
