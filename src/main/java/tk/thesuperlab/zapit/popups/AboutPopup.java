package tk.thesuperlab.zapit.popups;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tk.thesuperlab.zapit.ZapitApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AboutPopup {
	@FXML
	private Label labelVersion;

	@FXML
	protected void initialize() {
/*		Properties prop = new Properties();
		try(InputStream inputStream = ZapitApplication.class.getResourceAsStream("app.properties")) {
			prop.load(inputStream);
		} catch(IOException e) {
			e.printStackTrace();
		}

		labelVersion.setText(prop.getProperty("version"));*/
	}

	@FXML
	public void buttonCloseOnAction() {
		Stage stage = (Stage) labelVersion.getScene().getWindow();
		stage.close();
	}
}
