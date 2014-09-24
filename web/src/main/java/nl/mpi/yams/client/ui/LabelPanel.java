/**
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
package nl.mpi.yams.client.ui;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Peter Withers <peter.withers@mpi.nl>
 * @author twan.goosen@mpi.nl
 */
public class LabelPanel extends HorizontalPanel {
    
    public LabelPanel(String label, String value) {
        this(label, value, null);
    }
    
    public LabelPanel(String label, String value, String target) {
        final Label nameLabel = new Label(label);
        nameLabel.setStylePrimaryName("name-label");
        add(nameLabel);
        
        final Widget valueLabel;
        if (target == null) {
            valueLabel = new Label(value);
        } else {
            valueLabel = new Anchor(value, target);
        }
        valueLabel.setStylePrimaryName("value-label");
        add(valueLabel);
    }
    
    public static void addLabel(Panel panel, String name, String value) {
        panel.add(new LabelPanel(name, value));
    }
    
}
