package de.nils.conntest.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class UIStart
{
    private static final Logger log = LoggerFactory.getLogger(UIStart.class);

    public UIStart()
    {
        Platform.startup(this::startup);
    }

    private void startup()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/GU.fxml"));
            Scene scene = new Scene(loader.load());
        }
        catch(IOException e)
        {
            log.error("Failed to start UI", e);
        }
    }
}
