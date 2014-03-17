/*
 * Copyright (C) 2013 The Language Archive, Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package nl.mpi.yaas.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import java.util.ArrayList;

/**
 * @since Mar 17, 2014 3:05:36 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class TreeTableHeader {

    ArrayList<String> headerList = new ArrayList<String>();
    private final TreeItem treeItem;
    // todo: add resizable column headers
    private static final int columnoffset = 300;
    private static final int mainOffset = 400;

    public TreeTableHeader(TreeItem treeItem) {
        this.treeItem = treeItem;
        final HorizontalPanel outerPanel = (HorizontalPanel) treeItem.getWidget();
        final Style style = (outerPanel).getElement().getStyle();
        style.setLeft(0, Style.Unit.PX);
        style.setPosition(Style.Position.RELATIVE);
        final Label label = new Label();
        label.setWidth(mainOffset + "px");
        outerPanel.add(label);
    }

    public int getLeftForColumn(String columnName) {
        int columnIndex = headerList.indexOf(columnName);
        if (columnIndex == -1) {
            headerList.add(columnName);
            columnIndex = headerList.indexOf(columnName);
            final Label columnLabel = new Label(columnName);
            columnLabel.setWidth(columnoffset + "px");
            ((HorizontalPanel) treeItem.getWidget()).add(columnLabel);
        }
        return columnIndex * columnoffset + mainOffset;
    }
}
