package de.nils.conntest.gui.Components;

import de.nils.conntest.model.communication.Message;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class MessageCell extends ListCell<Message>
{
    public MessageCell()
    {
        super();
    }

    @Override
    public void updateItem(Message message, boolean empty)
    {
        super.updateItem(message, empty);

        if(empty || message == null)
        {
            setText(null);
            setGraphic(null);
        }
        else
        {
            switch(message.messageType())
            {
                case RECEIVED ->
                {
                    HBox hBox = new HBox();
                    Label messageBox = new Label();

                    messageBox.setText("-> " + message.message());
                    messageBox.setTooltip(new Tooltip("Source: " + message.source() + System.lineSeparator() + "Type: " + message.messageType()));

                    hBox.getChildren().add(messageBox);
                    setGraphic(hBox);
                }
                case SENT ->
                {
                    HBox hBox = new HBox();
                    Label messageBox = new Label();

                    messageBox.setText("<- " + message.message());
                    messageBox.setTooltip(new Tooltip("Source: " + message.source() + System.lineSeparator() + "Type: " + message.messageType()));

                    hBox.getChildren().add(messageBox);
                    setGraphic(hBox);
                }
                case INFORMATION ->
                {
                    BorderPane borderPane = new BorderPane();

                    borderPane.setCenter(new Label(message.message()));

                    setGraphic(borderPane);
                }
            }

            setText(null);
        }
    }
}
