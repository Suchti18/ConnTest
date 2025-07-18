package de.nils.conntest.gui;

import de.nils.conntest.common.Const;
import de.nils.conntest.gui.Components.ErrorAlert;
import de.nils.conntest.gui.Components.MessageCell;
import de.nils.conntest.gui.Components.MessageDialog;
import de.nils.conntest.gui.Components.NoSelectionModel;
import de.nils.conntest.model.communication.Message;
import de.nils.conntest.model.event.Event;
import de.nils.conntest.model.event.EventListener;
import de.nils.conntest.model.event.EventQueue;
import de.nils.conntest.model.event.EventType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
    private Label titleLabel;

    @FXML
    private BorderPane clientBorderPaneBtn;
    @FXML
    private BorderPane serverBorderPaneBtn;

    @FXML
    private TextField serverPort;
    @FXML
    private TextField clientAddress;
    @FXML
    private TextField clientPort;

    @FXML
    private Button serverStartBtn;
    @FXML
    private Button clientConnectBtn;

    @FXML
    private Button serverMessageBtn;
    @FXML
    private Button clientMessageBtn;

    @FXML
    private ListView<Message> serverMessages;
    @FXML
    private ListView<Message> clientMessages;

    // Content Panes
    @FXML
    private AnchorPane serverPane;
    @FXML
    private AnchorPane clientPane;

    private BorderPane previousSelectedBtn;
    private boolean serverStarted = false;
    private boolean clientConnected = false;

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
        serverMessages.setCellFactory(param -> new MessageCell());
        clientMessages.setCellFactory(param -> new MessageCell());

        serverMessages.setSelectionModel(new NoSelectionModel<>());
        clientMessages.setSelectionModel(new NoSelectionModel<>());

        serverMessages.setFocusTraversable(false);
        clientMessages.setFocusTraversable(false);

        doSelectServer();
    }

    @FXML
    public void doSelectClient()
    {
        select(clientBorderPaneBtn, clientPane, "Client");
    }

    @FXML
    public void doSelectServer()
    {
        select(serverBorderPaneBtn, serverPane, "Server");
    }

    public void select(BorderPane btn, AnchorPane contentPane, String title)
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

        titleLabel.setText(title);
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
            String port = serverPort.getText();

            EventQueue.getInstance().addEvent(
                    new Event(EventType.START_SERVER,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.SERVER_PORT_KEY, port)));
        }
    }

    @FXML
    public void doSendServerMessage()
    {
        log.debug("New server message button clicked");

        MessageDialog messageDialog = new MessageDialog();
        Optional<String> message = messageDialog.showAndWait();

        message.ifPresent(s ->
                EventQueue.getInstance().addEvent(
                    new Event(EventType.SERVER_MESSAGE_SENT,
                        System.currentTimeMillis(),
                        Map.of(Const.Event.MESSAGE_KEY, s))));
    }

    @FXML
    public void doStartClient()
    {
        if(clientConnected)
        {
            EventQueue.getInstance().addEvent(
                    new Event(EventType.STOP_CLIENT,
                            System.currentTimeMillis(),
                            null));
        }
        else
        {
            String port = clientPort.getText();
            String address = clientAddress.getText();

            EventQueue.getInstance().addEvent(
                    new Event(EventType.START_CLIENT,
                            System.currentTimeMillis(),
                            Map.of(Const.Event.CLIENT_ADDRESS_KEY, address,
                                    Const.Event.CLIENT_PORT_KEY, port)));
        }
    }

    @FXML
    public void doSendClientMessage()
    {
        log.debug("New client message button clicked");

        MessageDialog messageDialog = new MessageDialog();
        Optional<String> message = messageDialog.showAndWait();

        message.ifPresent(s ->
                EventQueue.getInstance().addEvent(
                    new Event(EventType.CLIENT_MESSAGE_SENT,
                        System.currentTimeMillis(),
                        Map.of(Const.Event.MESSAGE_KEY, s))));
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
                    serverStartBtn.setId("cancelBtn");
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
                    serverStartBtn.setId("initiateBtn");
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
                    
                    while(!messages.isEmpty())
                    {
                    	serverMessages.getItems().add(messages.poll());
                    }
                });
            }
            case CLIENT_STARTED ->
            {
                Platform.runLater(() ->
                {
                    clientAddress.setDisable(true);
                    clientPort.setDisable(true);
                    clientConnected = true;
                    clientConnectBtn.setText("Disconnect");
                    clientConnectBtn.setId("cancelBtn");
                    clientMessageBtn.setDisable(false);
                });
            }
            case CLIENT_STOPPED ->
            {
                Platform.runLater(() ->
                {
                    clientAddress.setDisable(false);
                    clientPort.setDisable(false);
                    clientConnected = false;
                    clientConnectBtn.setText("Connect");
                    clientConnectBtn.setId("initiateBtn");
                    clientMessageBtn.setDisable(true);
                });
            }
            case CLIENT_MESSAGE_RECEIVED ->
            {
                event.mustExist(Const.Event.ALL_MESSAGES_KEY);

                PriorityQueue<Message> messages = new PriorityQueue<>();
                messages.addAll(event.getData(Const.Event.ALL_MESSAGES_KEY));

                Platform.runLater(() ->
                {
                    clientMessages.getItems().clear();
                    
                    while(!messages.isEmpty())
                    {
                    	clientMessages.getItems().add(messages.poll());
                    }
                });
            }
            case ERROR ->
            {
            	event.mustExist(Const.Event.ERROR_TEXT);
            	
            	Platform.runLater(() ->
            	{
            		ErrorAlert alert = new ErrorAlert(event.getData(Const.Event.ERROR_TEXT));
            		alert.showAndWait();
            	});
            }
            default ->
            {
            }
        }
    }
}
