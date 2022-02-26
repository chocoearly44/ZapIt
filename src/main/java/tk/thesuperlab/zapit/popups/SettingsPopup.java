package tk.thesuperlab.zapit.popups;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;

public class SettingsPopup {
	@FXML
	public ToggleSwitch switchDarkMode;

	@FXML
	private void buttonSelectOnAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.showOpenDialog(switchDarkMode.getScene().getWindow());
	}

	@FXML
	private void buttonSaveOnAction() {

	}

	@FXML
	private void buttonCancelOnAction() {
		Stage stage = (Stage) switchDarkMode.getScene().getWindow();
		stage.close();
	}
}
