package de.nils.conntest.gui.Components;

import de.nils.conntest.gui.GuiResources;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class ErrorAlert extends Alert
{

	public ErrorAlert(String errorText)
	{
		super(AlertType.ERROR);
		
		setTitle("Error");
		setHeaderText("An error occured");
		setContentText(errorText);
		
		// Set Icon
		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(GuiResources.INSTANCE.getImage(GuiResources.logo));
	}
}
