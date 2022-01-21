package tk.thesuperlab.zapit.popups;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AboutPopup {
	@FXML
	private Label labelVersion;

	@FXML
	public void buttonCloseOnAction() {
		Stage stage = (Stage) labelVersion.getScene().getWindow();
		stage.close();
	}
}
