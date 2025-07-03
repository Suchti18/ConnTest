package de.nils.conntest.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable
{
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
        System.out.println(serverPort.getText());
    }
}
