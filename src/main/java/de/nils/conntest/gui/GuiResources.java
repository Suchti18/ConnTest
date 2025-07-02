package de.nils.conntest.gui;

import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GuiResources
{
    private static final Logger log = LoggerFactory.getLogger(GuiResources.class);
    public static final GuiResources INSTANCE = new GuiResources();

    public static final String logo = "ConnTestLogo.png";

    private final Map<String, Image> images;

    private GuiResources()
    {
        images = new HashMap<>();
    }

    public void loadImage(String filename)
    {
        // Check if the image already got loaded
        if(images.containsKey(filename))
        {
            return;
        }

        try
        {
            Image image = new Image(
                    Files.newInputStream(
                            Path.of(
                                    Objects.requireNonNull(
                                            getClass().getResource("/fxml/images/" + filename)
                                    ).toURI()
                            )
                    )
            );

            images.put(filename, image);
        }
        catch (URISyntaxException | IOException e)
        {
            log.error("Failed to load image <{}>", filename, e);
        }
    }

    public Image getImage(String filename)
    {
        return images.getOrDefault(filename, null);
    }
}
