/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.mpi.yams.annotator.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * @since Jan 16, 2014 8:17:37 PM (creation date)
 * @author Peter Withers <peter.withers@mpi.nl>
 */
public class AnnotationEditor extends RichTextArea {

//    public native void insertText(String text, int pos) /*-{
//     var $element = this.@com.google.gwt.user.client.ui.UIObject::getElement()();
//     var refNode = $element.contentWindow.getSelection().getRangeAt(0).endContainer;
//     refNode.insertData(pos, text);
//     }-*/;
//    // get element from point is untested and probably needs to start with the correct window or element
//    public native Element getElementFromPoint(int x, int y) /*-{            
//     var $element = this.@com.google.gwt.user.client.ui.UIObject::getElement()();
//     return $element.ownerDocument.elementFromPoint(x, y);
//     }-*/;
//    public native Element getElementFromPoint(Element target, int x, int y) /*-{
//     // return $wnd.document.elementFromPoint(x, y);
//     return target.ownerDocument.elementFromPoint(x, y);
//     }-*/;
//    public void addTimeCode(int start, int end) {
//        // StyleInjector appears to add styles to the entire document and might be what is needed               StyleInjector styleInjector = new StyleInjector();        
//        final String styleName = "cue" + start + "-" + end;
//        StyleInjector.inject(".myClass{color:red;}"); // this only injects it into the host page not the iframe
//        StyleInjector.injectAtEnd("div." + styleName + "{color:red;}", true);//style=\"color:green;\"
//        final String htmlToInsert = "<div class=\"myClass\">myClass</div><div class=\"" + styleName + "\" >styleName</div>\n"; //style=\"content: \"Section " + idTagCounter + ": \";\" 
//        final Formatter formatter = this.getFormatter();
//        formatter.insertHTML(htmlToInsert);
//    }
    public void insertText(String textToInsert) {
        final Formatter formatter = this.getFormatter();
        formatter.insertHTML(textToInsert);
    }

    public void insertImage(String imageToInsert) {
        final Formatter formatter = this.getFormatter();
        formatter.insertImage(imageToInsert);
    }
}
