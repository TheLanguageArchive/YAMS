package nl.mpi.yams.annotator.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import nl.mpi.yams.annotator.client.Messages;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class annotator implements EntryPoint {

    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    private static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";

    private final Messages messages = GWT.create(Messages.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        final Label errorLabel = new Label();

        // Add the nameField and sendButton to the RootPanel
        // Use RootPanel.get() to get the entire body element
        RootPanel.get("errorLabelContainer").add(errorLabel);

        // Create the popup dialog box
        final DialogBox dialogBox = new DialogBox();
        dialogBox.setText("Tools Pallet");
        dialogBox.setAnimationEnabled(true);
        final Label textToServerLabel = new Label();
        final HTML serverResponseLabel = new HTML();
        VerticalPanel dialogVPanel = new VerticalPanel();
        dialogVPanel.addStyleName("dialogVPanel");
        dialogVPanel.add(new HTML("<b>...</b>"));
        dialogVPanel.add(textToServerLabel);
        dialogVPanel.add(new HTML("<br><b>...</b>"));
        dialogVPanel.add(serverResponseLabel);
        dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
        dialogBox.setWidget(dialogVPanel);

        RootPanel.get("toolspallet").add(dialogBox);
        ////////////
        // Create the text area and toolbar
        final AnnotationEditor annotationEditor = new AnnotationEditor();
//        final VideoPanel videoPanel = new VideoPanel("Screen Shot 2014-01-16 at 7.46.33 PM.png", "SSL_LM_lex_b.mp4", "video/mp4");
        final VideoPanel videoPanel = new VideoPanel("images/sidebarl.png"); //, "http://hdl.handle.net/1839/00-0000-0000-000D-AB50-0", "video/m4a"
//        videoPanel.addSource("http://download.blender.org/peach/trailer/trailer_480p.mov", "video/mov");
//        videoPanel.addSource("http://download.blender.org/peach/trailer/trailer_400p.ogg", "video/ogg");
//        videoPanel.addSource("http://mirror.cessen.com/blender.org/peach/trailer/trailer_iphone.m4v", "video/m4v");
//        videoPanel.add(new HTML("<p>Video courtesy of <a href=\"http://www.bigbuckbunny.org/\" target=\"_blank\">Big Buck Bunny</a>.</p>"));
        videoPanel.addSource("http://www.w3schools.com/html/mov_bbb.mp4", "video/mp4");
        videoPanel.addSource("http://www.w3schools.com/html/mov_bbb.ogg", "video/ogg");
        RootPanel.get("videopanel").add(videoPanel);
        final AnnotationPanel annotationPanel = new AnnotationPanel(annotationEditor);
        Controller handler = new Controller(annotationPanel, annotationEditor, videoPanel, errorLabel, textToServerLabel, serverResponseLabel);
        annotationPanel.setController(handler);
//        annotationPanel.ensureDebugId("cwRichText-area");
        annotationPanel.setSize("100%", "14em");
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

        RootPanel.get("richtexteditor").add(annotationPanel);         ////////////////////

        // Focus the cursor on the name field when the app loads
        annotationPanel.setFocus(true);
    }
}
