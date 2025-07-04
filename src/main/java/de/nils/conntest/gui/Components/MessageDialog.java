package de.nils.conntest.gui.Components;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;

public class MessageDialog extends Dialog<String>
{
    public MessageDialog()
    {
        super();

        setTitle("New Message");
        setHeaderText("Send a new message");

        ButtonType sendButtonType = new ButtonType("Send", ButtonBar.ButtonData.FINISH);
        getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);

        Node sendButton = getDialogPane().lookupButton(sendButtonType);
        sendButton.setDisable(true);

        TextArea textArea = new TextArea();

        textArea.textProperty().addListener((observable, oldValue, newValue) ->
        {
            sendButton.setDisable(textArea.getText().isEmpty());
        });

        getDialogPane().setContent(textArea);

        Platform.runLater(textArea::requestFocus);

        setResultConverter(dialogBtn ->
        {
            if(dialogBtn == sendButtonType)
            {
                return textArea.getText();
            }

            return null;
        });
    }
}
