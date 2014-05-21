/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yaas.annotator.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @since Jan 24, 2014 3:28:52 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class AnnotationPanel extends VerticalPanel {

    final private AnnotationEditor annotationEditor;
    private Controller controller;

    public AnnotationPanel(AnnotationEditor annotationEditor) {
//        this.handler = handler;        

        ////////////
        // Create the text area and toolbar
        this.annotationEditor = annotationEditor;
        add(annotationEditor);
        annotationEditor.ensureDebugId("cwRichText-area");
        annotationEditor.setSize("100%", "14em");
//        RichTextToolbar toolbar = new RichTextToolbar(area);
//        toolbar.ensureDebugId("cwRichText-toolbar");
//        toolbar.setWidth("100%");
//        final RichTextArea.Formatter formatter = richTextAarea.getFormatter();
//        formatter.insertHTML("<style>\n"
//                + "body {counter-reset:section;}\n"
//                + "div:before\n"
//                + "{\n"
//                + "counter-increment:section;\n"
//                + "content:\"Section \" counter(section) \". \";\n"
//                + "}\n"
//                + "</style>");
    }

    public void setController(Controller controller) {
        this.controller = controller;
        annotationEditor.addClickHandler(controller);
        annotationEditor.addKeyUpHandler(controller);
        AnnotatorButtonsBar annotatorButtonsBar = new AnnotatorButtonsBar(controller);
        add(annotatorButtonsBar);
    }

    public AnnotationEditor getAnnotationEditor() {
        return annotationEditor;
    }

    public void insertAnnotationBlock(final AnnotationData annotationData) {
        HTML annotationBlock = new HTML(annotationData.getAnnotationHtml());
        annotationBlock.setStyleName("annotationblock");
        annotationBlock.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                controller.clickSegment(annotationData);
            }
        });
        this.insert(annotationBlock, this.getWidgetCount() - 2);
    }

    public void setFocus(boolean state) {
        annotationEditor.setFocus(state);
    }
}
