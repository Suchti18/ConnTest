package de.nils.conntest.gui;

import de.nils.conntest.common.Const;
import de.nils.conntest.gui.Components.MessageCell;
import de.nils.conntest.gui.Components.MessageDialog;
import de.nils.conntest.model.communication.Message;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.ResourceBundle;

public class MainController implements Initializable, EventListener
{
    private static final Logger log = LoggerFactory.getLogger(MainController.class);

    @FXML
    private BorderPane clientBorderPaneBtn;
    @FXML
    private BorderPane serverBorderPaneBtn;

    @FXML
    private TextField serverPort;
    @FXML
    private Button serverStartBtn;
    @FXML
    private Button serverMessageBtn;
    @FXML
    private ListView<Message> serverMessages;

    // Content Panes
    @FXML
    private AnchorPane serverPane;
    @FXML
    private AnchorPane clientPane;

    private BorderPane previousSelectedBtn;
    private boolean serverStarted = false;

    public MainController()
    {
        EventQueue.getInstance().addListener(this);
    }

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
        serverMessages.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>()
        {
            @Override
            public ListCell<Message> call(ListView<Message> param)
            {
                return new MessageCell();
            }
        });

        doSelectServer();
    }

    @FXML
    public void doSelectClient()
    {
        select(clientBorderPaneBtn, clientPane);
    }

    @FXML
    public void doSelectServer()
    {
        select(serverBorderPaneBtn, serverPane);
    }

    public void select(BorderPane btn, AnchorPane contentPane)
    {
        if(previousSelectedBtn != null)
        {
            previousSelectedBtn.setId("");
        }

        btn.setId("selectedBtn");
        previousSelectedBtn = btn;

        for(Node node : contentPane.getParent().getChildrenUnmodifiable())
        {
            node.setVisible(node == contentPane);
        }
    }

    @FXML
    public void doStartServer()
    {
        if(serverStarted)
        {
            EventQueue.getInstance().addEvent(
                    new Event(EventType.STOP_SERVER,
                            System.currentTimeMillis(),
                            null));
        }
        else
        {
            try
            {
                int port = Integer.parseInt(serverPort.getText());

                EventQueue.getInstance().addEvent(
                        new Event(EventType.START_SERVER,
                                System.currentTimeMillis(),
                                Map.of(Const.Event.SERVER_PORT_KEY, port)));
            }
            catch(NumberFormatException e)
            {
                log.error("User entered a not numeric port and tried to start a server", e);
            }
        }
    }

    @FXML
    public void doSendServerMessage()
    {
        log.debug("New server message button clicked");

        MessageDialog messageDialog = new MessageDialog();
        Optional<String> message = messageDialog.showAndWait();

        if(message.isPresent())
        {
            EventQueue.getInstance().addEvent(
                    new Event(EventType.SERVER_MESSAGE_SENT,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.MESSAGE_KEY, message.get())));
        }
    }

    @Override
    public void handleEvent(Event event)
    {
        switch(event.eventType())
        {
            case SERVER_STARTED ->
            {
                Platform.runLater(() ->
                {
                    serverPort.setDisable(true);
                    serverStarted = true;
                    serverStartBtn.setText("Stop");
                    serverMessageBtn.setDisable(false);
                });
            }
            case SERVER_STOPPED ->
            {
                Platform.runLater(() ->
                {
                    serverPort.setDisable(false);
                    serverStarted = false;
                    serverStartBtn.setText("Start");
                    serverMessageBtn.setDisable(true);
                });
            }
            case SERVER_MESSAGE_RECEIVED ->
            {
                event.mustExist(Const.Event.ALL_MESSAGES_KEY);

                PriorityQueue<Message> messages = new PriorityQueue<>();
                messages.addAll(event.getData(Const.Event.ALL_MESSAGES_KEY));

                Platform.runLater(() ->
                {
                    serverMessages.getItems().clear();
                    serverMessages.getItems().addAll(messages);
                });
            }
        }
    }
}
