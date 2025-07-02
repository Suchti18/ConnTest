package de.nils.conntest.gui;

import de.nils.conntest.common.Const;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
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
            Stage stage = new Stage();

            FXMLLoader loader = new FXMLLoader(this.getClass().getResource(Const.GUI.FXML_FILE_PATH));
            loader.setController(new MainController());
            Scene scene = new Scene(loader.load());

            stage.setScene(scene);
            stage.setTitle("ConnTest");

            GuiResources.INSTANCE.loadImage(GuiResources.logo);

            stage.getIcons().add(GuiResources.INSTANCE.getImage(GuiResources.logo));
            addAppToTray();

            stage.setResizable(true);
            stage.show();
        }
        catch(IllegalStateException | IOException e)
        {
            log.error("Failed to start UI", e);

            // Try to stop all FX threads
            Platform.runLater(Platform::exit);
        }
    }

    private void addAppToTray()
    {
        try
        {
            Toolkit.getDefaultToolkit();

            if(SystemTray.isSupported())
            {
                SystemTray tray = SystemTray.getSystemTray();

                TrayIcon trayIcon = new TrayIcon(SwingFXUtils.fromFXImage(GuiResources.INSTANCE.getImage(GuiResources.logo), null));
                trayIcon.setToolTip("ConnTest");
                trayIcon.setImageAutoSize(true);

                tray.add(trayIcon);
            }
            else
            {
                log.info("SystemTray is not supported");
            }
        }
        catch(AWTException e)
        {
            log.error("Failed to add tray icon", e);
        }
    }
}
