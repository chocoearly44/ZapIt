package tk.thesuperlab.zapit.popups;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import tk.thesuperlab.zapit.entities.Config;

import static tk.thesuperlab.zapit.ZapitApplication.storageUtils;

public class SettingsPopup {
	private Config config;

	@FXML
	public ToggleSwitch switchDarkMode;

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
		fileChooser.showOpenDialog(switchDarkMode.getScene().getWindow());
	}

	@FXML
	private void buttonSaveOnAction() {
		if(!config.getWorkspacePath().equals(fieldWorkspacePath.getText())) {
			config.setDarkMode(switchDarkMode.isSelected());
			config.setWorkspacePath(fieldWorkspacePath.getText());
		}

		storageUtils.saveConfig(config);

		Stage stage = (Stage) switchDarkMode.getScene().getWindow();
		stage.close();
	}

	@FXML
	private void buttonCancelOnAction() {
		Stage stage = (Stage) switchDarkMode.getScene().getWindow();
		stage.close();
	}
}
