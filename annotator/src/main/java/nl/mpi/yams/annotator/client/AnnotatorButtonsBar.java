/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.annotator.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @since Jan 24, 2014 3:18:22 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class AnnotatorButtonsBar extends VerticalPanel {

    final private Label inTime = new Label("00:00:00");
    final Label outTime = new Label("00:00:00");

    public AnnotatorButtonsBar(final Controller controller) {
        final HorizontalPanel timePanel = new HorizontalPanel();
        final HorizontalPanel formatPanel = new HorizontalPanel();
        final HorizontalPanel addPanel = new HorizontalPanel();
        timePanel.add(inTime);
        timePanel.add(new Label("   -   "));
        timePanel.add(outTime);
        timePanel.add(new Button("Set IN", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.setInPoint(AnnotatorButtonsBar.this);
            }
        }));
        timePanel.add(new Button("Set OUT", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.setOutPoint(AnnotatorButtonsBar.this);
            }
        }));
        formatPanel.add(new Button("<b>bold</b>", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.addString("Bold not implemented yet.");
            }
        }));
        formatPanel.add(new Button("<i>italic</i>", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.addString("Italic not implemented yet.");
            }
        }));
        formatPanel.add(new Button("<sub>subscript</sub>", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.addString("Subscript not implemented yet.");
            }
        }));
        formatPanel.add(new Button("<sup>superscript</sup>", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.addString("Superscript not implemented yet.");
            }
        }));
        addPanel.add(new Button("Add Audio", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.addString("Add audio not implemented yet.");
            }
        }));
        addPanel.add(new Button("Add Video", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.addString("Add video not implemented yet.");
            }
        }));
        addPanel.add(new Button("Add Photo", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.addImage("https://tla.mpi.nl/wp-content/uploads/2011/12/tools.png");
            }
        }));
        addPanel.add(new Button("Add Location", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.addString("Add Location not implemented yet.");
            }
        }));
        addPanel.add(new Button("Insert Current Text", new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.addSegment();
            }
        }));
        this.add(timePanel);
        this.add(formatPanel);
        this.add(addPanel);
    }

    public void setInTime(double time) {
        inTime.setText(formatTimeCode(time));
    }

    public void setOutTime(double time) {
        outTime.setText(formatTimeCode(time));
    }

    private String formatTimeCode(double time) {
        return Math.floor(time / 60) + ":" + Math.floor(time % 60) + ":" + Math.floor(time % 1 * 100);
    }
}
