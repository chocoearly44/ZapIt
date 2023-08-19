package tk.thesuperlab.zapit.popups;

import atlantafx.base.controls.ToggleSwitch;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tk.thesuperlab.zapit.ZapitController;
import tk.thesuperlab.zapit.entities.Connection;

import java.util.UUID;

import static tk.thesuperlab.zapit.ZapitApplication.storageUtils;
import static tk.thesuperlab.zapit.ZapitApplication.workspace;

public class ConnectionPopup {
	private final ZapitController zapitController;

	@FXML
	private Button buttonAdd;
	@FXML
	private Button buttonCancel;
	@FXML
	private TextField fieldName;
	@FXML
	private ComboBox<String> comboProtocol;
	@FXML
	private TextField fieldHost;
	@FXML
	private TextField fieldPort;
	@FXML
	private ToggleSwitch switchClean;
	@FXML
	private ToggleSwitch switchReconnect;
	@FXML
	private TextField fieldAlive;
	@FXML
	private TextField fieldClientId;
	@FXML
	private TextField fieldUsername;
	@FXML
	private TextField fieldPassword;

	public ConnectionPopup(ZapitController zapitController) {
		this.zapitController = zapitController;
	}

	@FXML
	public void initialize() {
		fieldClientId.setText(randomClientId());
	}

	@FXML
	public void buttonRandomIdOnAction() {
		fieldClientId.setText(randomClientId());
	}

	@FXML
	public void buttonAddOnAction() {
		// Add connection
		String hostname = comboProtocol.getValue() + fieldHost.getText() + ":" + fieldPort.getText();

		workspace.getConnections().add(
				new Connection(
						fieldName.getText(),
						hostname,
						fieldClientId.getText(),
						Integer.parseInt(fieldAlive.getText()),
						switchClean.isSelected(),
						switchReconnect.isSelected(),
						fieldUsername.getText(),
						fieldPassword.getText()
				)
		);

		// Save workspace
		storageUtils.saveWorkspace(workspace);

		Stage stage = (Stage) buttonAdd.getScene().getWindow();
		zapitController.refreshConnections();
		stage.close();
	}

	@FXML
	public void buttonCancelOnAction() {
		Stage stage = (Stage) buttonCancel.getScene().getWindow();
		stage.close();
	}

	private String randomClientId() {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		return "zapit_" + uuid;
	}
}
