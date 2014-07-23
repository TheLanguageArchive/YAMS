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
package nl.mpi.yams.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @since Feb 12, 2014 5:19:22 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class ResourceViewer extends VerticalPanel {

    public ResourceViewer(final String resourceHandle) {
        final String targetUrl = resourceHandle.replace("hdl:", "http://hdl.handle.net/");
        Anchor openLink = new Anchor("open");
        this.add(openLink);
        openLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                Window.open(targetUrl, "_blank", "");
            }
        });
        Anchor viewLink = new Anchor("view");
        viewLink.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showUrl(targetUrl);
            }
        });
        this.add(viewLink);
    }

    public void showUrl(String targetUrl) {
        final Frame frame = new Frame(targetUrl);
        frame.setStyleName("yams-media-viewer");
        final HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.setHorizontalAlignment(ALIGN_RIGHT);
        horizontalPanel.setSpacing(20);
        Anchor maximise = new Anchor("maximise");
        maximise.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                AbsolutePanel absolutePanel = new AbsolutePanel();
                ResourceViewer.this.remove(frame);
                ResourceViewer.this.remove(horizontalPanel);
                final PopupPanel panel = new PopupPanel(true, true);
                frame.setStyleName("yams-media-viewer-full-screen");
                absolutePanel.add(frame);
                Anchor close = new Anchor("close");
                close.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        panel.hide();
                    }
                });
                close.setStyleName("yams-media-viewer-close-button");
                absolutePanel.add(close);
                panel.add(absolutePanel);
                panel.show();
            }
        });
        horizontalPanel.add(maximise);
        Anchor close = new Anchor("close");
        close.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                ResourceViewer.this.remove(frame);
                ResourceViewer.this.remove(horizontalPanel);
            }
        });
        horizontalPanel.add(close);
        this.add(horizontalPanel);
        this.add(frame);
    }
}
