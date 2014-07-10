/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.annotator.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * @since Jan 24, 2014 3:42:50 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class Controller implements ClickHandler, KeyUpHandler {

    final AnnotationPanel annotationPanel;
    final AnnotationEditor annotationEditor;
    final VideoPanel videoPanel;
    final Label errorLabel;
    final Label textToServerLabel;
    final HTML serverResponseLabel;
    private int idTagCounter = 0; // testing variable to be replaced 
//    static private int currentTimeCode = -1;

    private AnnotationData annotationData = new AnnotationData();

    public Controller(AnnotationPanel annotationPanel, AnnotationEditor annotationEditor, VideoPanel videoPanel, Label errorLabel, Label textToServerLabel, HTML serverResponseLabel) {
        this.annotationPanel = annotationPanel;
        this.annotationEditor = annotationEditor;
        this.errorLabel = errorLabel;
        this.textToServerLabel = textToServerLabel;
        this.serverResponseLabel = serverResponseLabel;
        this.videoPanel = videoPanel;
    }

    /**
     * Fired when the user clicks on the text area.
     */
    public void onClick(ClickEvent event) {
        sendNameToServer(event.toDebugString());
//        final Element element = annotationEditor.getElementFromPoint(event.getRelativeElement(), event.getX(), event.getY());
//        if (element == null) {
//            textToServerLabel.setText("null");
//        } else {
//            textToServerLabel.setText(element.getInnerHTML());
//        }
    }

    /**
     * Fired when the user types in the text area.
     */
    public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            sendNameToServer(event.toDebugString());
//                    final AnnotationPanel.Formatter formatter = annotationPanel.getFormatter();
//                    final String htmlToInsert = "<div class=\"testclass" + idTagCounter + "\" id=\"" + idTagCounter++ + "\"></div>\n"; //style=\"content: \"Section " + idTagCounter + ": \";\" 
//                    final String htmlToInsert = "section";
//                    textToServerLabel.setText(htmlToInsert);
//                    formatter.insertHTML(htmlToInsert);
//                    annotationPanel.insertText(htmlToInsert, annotationPanel.getText().length());
//            annotationEditor.addTimeCode(++idTagCounter, ++idTagCounter);
        } else {
            textToServerLabel.setText(event.getRelativeElement().getInnerHTML());
        }
        // walk the div elements and insert id/class as required
//                final NodeList<Element> divElements = richTextAarea.getElement().getElementsByTagName("div");
//                for (int index = 0; index < divElements.getLength(); index++) {
//                    final Element divItem = divElements.getItem(index);
//                    if (divItem.getClassName().isEmpty()) {
//                        divItem.setClassName("testclass" + ++idTagCounter);
//                    }
//                }
        // show the raw html 
        serverResponseLabel.setText(annotationEditor.getHTML());
    }

    /**
     * Send the name from the nameField to the server and wait for a response.
     */
    private void sendNameToServer(String message) {
        // First, we validate the input.
        errorLabel.setText("");
//                String textToServer = richTextAarea.getText();
//                errorLabel.setText(message);

        // Then, we send the input to the server.
//                textToServerLabel.setText(textToServer);
//                serverResponseLabel.setText("");
//                greetingService.greetServer(textToServer, new AsyncCallback<String>() {
//                    public void onFailure(Throwable caught) {
//                        // Show the RPC error message to the user
//                        dialogBox.setText("Remote Procedure Call - Failure");
//                        serverResponseLabel.addStyleName("serverResponseLabelError");
//                        serverResponseLabel.setHTML(SERVER_ERROR);
//                        dialogBox.center();
//                        closeButton.setFocus(true);
//                    }
//
//                    public void onSuccess(String result) {
//                        dialogBox.setText("Remote Procedure Call");
//                        serverResponseLabel.removeStyleName("serverResponseLabelError");
        serverResponseLabel.setHTML(message);
//                        dialogBox.center();
//                        closeButton.setFocus(true);
//                    }
//                });
    }

//    public static void setCurrentTimeCode(int timecode) {
//        currentTimeCode = timecode;
//    }
//
//    public static native void bindVideoEvents() /*-{
//     //http://www.w3.org/TR/html5/embedded-content-0.html#event-media-ended
//     $wnd.setCurrentTimeCode = $entry(@mypackage.MyUtilityClass::setCurrentTimeCode(I));
//     $('video').get(0).bind('timeupdate', function(){
//     setCurrentTimeCode($('video').get(0).currentTime);
//     }-*/;
    public void setInPoint(AnnotatorButtonsBar annotatorButtonsBar) {
        annotationData.setInTime(videoPanel.getCurrentTime());
        annotatorButtonsBar.setInTime(annotationData.getInTime());
    }

    public void setOutPoint(AnnotatorButtonsBar annotatorButtonsBar) {
        annotationData.setOutTime(videoPanel.getCurrentTime());
        annotatorButtonsBar.setOutTime(annotationData.getOutTime());
    }

    public void addSegment() {
        annotationData.setAnnotationHtml(annotationEditor.getHTML());
        annotationPanel.insertAnnotationBlock(annotationData);
        annotationEditor.setHTML("");
        annotationData = new AnnotationData(annotationData.getInTime(), annotationData.getOutTime(), "");
//    annotationBlock.addHandler(handler, KeyDownEvent.getType());
    }

    public void addString(String text) {
        annotationEditor.insertText(text);
    }

    public void addImage(String iageUrl) {
        annotationEditor.insertImage(iageUrl);
    }

    public void clickSegment(AnnotationData annotationData) {
        videoPanel.playSegment(annotationData);
    }
}
