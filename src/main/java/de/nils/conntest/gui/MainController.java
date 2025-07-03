package de.nils.conntest.gui;

import de.nils.conntest.common.Const;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable
{
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private BorderPane clientBorderPaneBtn;
    @FXML
    private BorderPane serverBorderPaneBtn;

    @FXML
    private TextField serverPort;

    private BorderPane previousSelectedBtn;

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        select(serverBorderPaneBtn);
    }

    @FXML
    public void doSelectClient()
    {
        select(clientBorderPaneBtn);
    }

    @FXML
    public void doSelectServer()
    {
        select(serverBorderPaneBtn);
    }

    public void select(BorderPane btn)
    {
        if(previousSelectedBtn != null)
        {
            previousSelectedBtn.setStyle("");
        }

        btn.setStyle("-fx-background-color: limegreen");
        previousSelectedBtn = btn;
    }

    @FXML
    public void doConnect()
    {
        try
        {
            int port = Integer.parseInt(serverPort.getText());

            EventQueue.getInstance().addEvent(new Event(EventType.START_SERVER, System.currentTimeMillis(), Map.of(Const.Event.SERVER_PORT_KEY, port)));
        }
        catch(NumberFormatException e)
        {
            log.error("User entered a not numeric port");
        }
    }
}
