package de.nils.conntest.gui;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainController
{
    @FXML
    private TextField serverPort;

    @FXML
    public void doConnect()
    {
        System.out.println(serverPort.getText());
    }
}
