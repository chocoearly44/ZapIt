package tk.thesuperlab.zapit.popups;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import tk.thesuperlab.zapit.config.Theme;

import java.io.File;

import static tk.thesuperlab.zapit.ZapitApplication.*;

public class Settings {

	@FXML
	private TextField fieldWorkspacePath;
	@FXML
	private ComboBox<Theme> themeSelect;

	@FXML
	private void initialize() {
		// Set values
		fieldWorkspacePath.setText(globalConfig.getCurrentWorkspace().getAbsolutePath());
		themeSelect.getItems().addAll(Theme.values());
		themeSelect.getSelectionModel().select(workspace.getTheme());
	}

	@FXML
	private void buttonSelectOnAction() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Open Workspace");
		directoryChooser.setInitialDirectory(globalConfig.getCurrentWorkspace());
		File selectedDirectory = directoryChooser.showDialog(themeSelect.getScene().getWindow());

		if(selectedDirectory != null) {
			fieldWorkspacePath.setText(selectedDirectory.getAbsolutePath());
		}
	}

	@FXML
	private void buttonSaveOnAction() {
		Alert alert = new Alert(Alert.AlertType.WARNING, "ZapIt will now close to apply your settings.", ButtonType.OK);
		alert.showAndWait();

		if(alert.getResult() == ButtonType.OK) {
			globalConfig.setCurrentWorkspace(new File(fieldWorkspacePath.getText()));
			globalConfigurator.saveConfig(globalConfig);

			workspace.setTheme(themeSelect.getValue());
			workspaceConfigurator.saveConfig("config.json", workspace);

			Platform.exit();
			System.exit(0);
		}
	}

	@FXML
	private void buttonCancelOnAction() {
		Stage stage = (Stage) themeSelect.getScene().getWindow();
		stage.close();
	}
}
