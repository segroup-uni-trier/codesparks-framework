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

import com.intellij.ui.table.JBTable;

import javax.swing.table.TableModel;

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
