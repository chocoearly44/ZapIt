package tk.thesuperlab.zapit.popups;

import atlantafx.base.controls.ToggleSwitch;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tk.thesuperlab.zapit.entities.Connection;
import tk.thesuperlab.zapit.pages.Homepage;

import java.util.UUID;

import static tk.thesuperlab.zapit.ZapitApplication.workspace;
import static tk.thesuperlab.zapit.ZapitApplication.workspaceConfigurator;

public class EditConnection {
	private final Homepage homepageController;

	private Connection connection;

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

	public EditConnection(Homepage homepageController, Connection connection) {
		this.homepageController = homepageController;
		this.connection = connection;
	}

	@FXML
	public void initialize() {
		if(connection != null) {
			fieldName.setText(connection.getName());
			fieldClientId.setText(connection.getClientId());
			fieldAlive.setText(String.valueOf(connection.getKeepAlive()));
			switchClean.setSelected(connection.isCleanSession());
			switchReconnect.setSelected(connection.isAutoReconnect());
			fieldUsername.setText(connection.getUsername());
			fieldPassword.setText(connection.getPassword());
		} else {
			fieldClientId.setText(randomClientId());
		}
	}

	@FXML
	public void buttonRandomIdOnAction() {
		fieldClientId.setText(randomClientId());
	}

	@FXML
	public void buttonAddOnAction() {
		// Add connection
		String hostname = comboProtocol.getValue() + fieldHost.getText() + ":" + fieldPort.getText();

		if(connection == null) {
			connection = new Connection();
			workspace.getConnections().add(connection);
		}

		connection.setName(fieldName.getText());
		connection.setHostname(hostname);
		connection.setClientId(fieldClientId.getText());
		connection.setKeepAlive(Integer.parseInt(fieldAlive.getText()));
		connection.setCleanSession(switchClean.isSelected());
		connection.setAutoReconnect(switchReconnect.isSelected());
		connection.setUsername(fieldUsername.getText());
		connection.setPassword(fieldPassword.getText());

		workspaceConfigurator.saveConfig("config.json", workspace);

		homepageController.refreshConnections();
		Stage stage = (Stage) buttonAdd.getScene().getWindow();
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
