package de.nils.conntest.gui.Components;

import de.nils.conntest.model.communication.Message;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
            HBox hBox = new HBox();

            Label messageBox = new Label();

            switch(message.messageType())
            {
                case RECEIVED ->
                {
                    messageBox.setText("> " + message.message());
                }
                case SENT ->
                {
                    messageBox.setText("< " + message.message());
                }
                case INFORMATION ->
                {
                    messageBox.setText("<" + message.message() + ">");
                }
            }

            hBox.getChildren().add(messageBox);

            setText(null);
            setGraphic(hBox);
        }
    }
}
