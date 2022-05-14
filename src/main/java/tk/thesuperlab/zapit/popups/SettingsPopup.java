package tk.thesuperlab.zapit.popups;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import tk.thesuperlab.zapit.entities.Config;

import java.io.File;

import static tk.thesuperlab.zapit.ZapitApplication.storageUtils;

public class SettingsPopup {
	@FXML
	public ToggleSwitch switchDarkMode;
	private Config config;
	@FXML
	private TextField fieldWorkspacePath;

	@FXML
	private void initialize() {
		config = storageUtils.getConfig();

		switchDarkMode.setSelected(config.isDarkMode());
		fieldWorkspacePath.setText(config.getWorkspacePath());
	}

	@FXML
	private void buttonSelectOnAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Workspace");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZapIt Workspace", "*.zwp"));
		File newWorkspace = fileChooser.showOpenDialog(switchDarkMode.getScene().getWindow());

		if(newWorkspace != null) {
			fieldWorkspacePath.setText(newWorkspace.getAbsolutePath());
		}
	}

	@FXML
	private void buttonSaveOnAction() {
		Alert alert = new Alert(Alert.AlertType.WARNING, "ZapIt will now close to apply your settings.", ButtonType.CANCEL, ButtonType.OK);
		alert.showAndWait();

		if(alert.getResult() == ButtonType.OK) {
			config.setWorkspacePath(fieldWorkspacePath.getText());
			config.setDarkMode(switchDarkMode.isSelected());
			storageUtils.saveConfig(config);

			Platform.exit();
			System.exit(0);
		} else {
			Stage stage = (Stage) switchDarkMode.getScene().getWindow();
			stage.close();
		}
	}

	@FXML
	private void buttonCancelOnAction() {
		Stage stage = (Stage) switchDarkMode.getScene().getWindow();
		stage.close();
	}
}
