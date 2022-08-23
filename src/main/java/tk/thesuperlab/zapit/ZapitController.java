package tk.thesuperlab.zapit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import tk.thesuperlab.zapit.entities.Connection;
import tk.thesuperlab.zapit.entities.Message;
import tk.thesuperlab.zapit.popups.ConnectionPopup;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import static tk.thesuperlab.zapit.ZapitApplication.*;

public class ZapitController {
	private IMqttClient mqttClient;

	@FXML
	private ListView<String> listConnections;
	@FXML
	private Label labelName;
	@FXML
	private TextField fieldSendTopic;
	@FXML
	private Button buttonSend;
	@FXML
	private TextArea areaMessage;
	@FXML
	private Button buttonSubscribe;
	@FXML
	private TextField fieldReceiveTopic;
	@FXML
	private Accordion accordionSubs;

	@FXML
	public void initialize() {
		refreshConnections();

		/*menuNewWorkspace.setOnAction(event -> {});
		menuOpenWorkspace.setOnAction(event -> {});
		menuSaveWorkspace.setOnAction(event -> {});
		menuSaveWorkspaceAs.setOnAction(event -> {});*/
	}

	@FXML
	public void buttonAddServerOnAction() throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("connection-popup.fxml"));
		Parent scene = fxmlLoader.load();

		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setTitle("Add connection");
		stage.getIcons().add(new Image(ZapitController.class.getResourceAsStream("icon.png")));
		stage.setScene(new Scene(scene));

		JMetro jMetroConnection;
		if(config.isDarkMode()) {
			jMetroConnection = new JMetro(Style.DARK);
		} else {
			jMetroConnection = new JMetro(Style.LIGHT);
		}

		jMetroConnection.setParent(scene);

		ConnectionPopup connectionPopup = fxmlLoader.getController();
		connectionPopup.init(this);

		stage.show();
	}

	@FXML
	public void buttonConnectOnAction() {
		if(listConnections.getSelectionModel().getSelectedItem() != null) {
			connect();
		}
	}

	@FXML
	public void buttonSubscribeOnAction() {
		subscribeTopic(fieldReceiveTopic.getText());
	}

	@FXML
	public void buttonSendOnAction() {
		try {
			MqttMessage msg = new MqttMessage(areaMessage.getText().getBytes(StandardCharsets.UTF_8));
			msg.setQos(0);
			msg.setRetained(true);
			mqttClient.publish(fieldSendTopic.getText(), msg);
		} catch(MqttException e) {
			showError("There was an MQTT error.", e.getMessage());
		}
	}

	//TODO: connection editing support
	/*@FXML
	public void menuConnectionsEditOnAction() {
		System.out.println("test");
	}*/

	@FXML
	public void menuConnectionsRemoveOnAction() {
		String selectedItem = listConnections.getSelectionModel().getSelectedItem();

		if(selectedItem != null) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure that you want to delete " + selectedItem + " ?", ButtonType.NO, ButtonType.YES);
			alert.showAndWait();

			if(alert.getResult() == ButtonType.YES) {
				stopMqtt();
				disableUi();
				int index = listConnections.getSelectionModel().getSelectedIndex();
				workspace.getConnections().remove(index);
				storageUtils.saveWorkspace(workspace);
				refreshConnections();
			}
		}
	}

	@FXML
	public void menuPreferencesOnAction() throws IOException {
		FXMLLoader aboutLoader = new FXMLLoader(getClass().getResource("settings-popup.fxml"));
		Parent aboutScene = aboutLoader.load();

		Stage aboutStage = new Stage();
		aboutStage.initModality(Modality.APPLICATION_MODAL);
		aboutStage.setTitle("ZapIt settings");
		aboutStage.getIcons().add(new Image(ZapitController.class.getResourceAsStream("icon.png")));
		aboutStage.setScene(new Scene(aboutScene));

		JMetro jMetroSettings;
		if(config.isDarkMode()) {
			jMetroSettings = new JMetro(Style.DARK);
		} else {
			jMetroSettings = new JMetro(Style.LIGHT);
		}

		jMetroSettings.setParent(aboutScene);

		aboutStage.show();
	}

	@FXML
	public void menuExitOnAction() {
		stopMqtt();

		Stage app = (Stage) buttonSubscribe.getScene().getWindow();
		app.close();
	}

	@FXML
	public void menuAboutOnAction() throws IOException {
		FXMLLoader aboutLoader = new FXMLLoader(getClass().getResource("about-popup.fxml"));
		Parent aboutScene = aboutLoader.load();

		Stage aboutStage = new Stage();
		aboutStage.initModality(Modality.APPLICATION_MODAL);
		aboutStage.setTitle("About");
		aboutStage.getIcons().add(new Image(ZapitController.class.getResourceAsStream("icon.png")));
		aboutStage.setScene(new Scene(aboutScene));

		JMetro jMetroAbout;
		if(config.isDarkMode()) {
			jMetroAbout = new JMetro(Style.DARK);
		} else {
			jMetroAbout = new JMetro(Style.LIGHT);
		}

		jMetroAbout.setParent(aboutScene);

		aboutStage.show();
	}

	public void subscribeTopic(String subTopic) {
		VBox vBoxRoot = new VBox();
		TitledPane titledPane = new TitledPane(subTopic, vBoxRoot);
		titledPane.setAnimated(false);

		// Table view
		TableView<Message> tableView = new TableView<Message>();
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn columnTimestamp = new TableColumn("Timestamp");
		columnTimestamp.setCellValueFactory(new PropertyValueFactory<Message, Long>("timestamp"));
		columnTimestamp.setSortType(TableColumn.SortType.DESCENDING);

		TableColumn columnTime = new TableColumn("Time");
		columnTime.setCellValueFactory(new PropertyValueFactory<Message, String>("time"));
		columnTime.setSortable(false);

		TableColumn columnMessage = new TableColumn("Message");
		columnMessage.setCellValueFactory(new PropertyValueFactory<Message, String>("message"));
		columnMessage.setSortable(false);

		tableView.getColumns().addAll(columnTimestamp, columnTime, columnMessage);
		tableView.getSortOrder().add(columnTimestamp);

		// Button bar
		HBox hBoxButtonBar = new HBox();
		hBoxButtonBar.setSpacing(8.0);
		hBoxButtonBar.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);

		Button buttonClear = new Button("Clear");
		buttonClear.setDefaultButton(true);
		buttonClear.setOnAction(event -> {
			tableView.getItems().clear();
			tableView.refresh();
		});

		Button buttonUnsubscribe = new Button("Unsubscribe");
		buttonUnsubscribe.setOnAction(event -> {
			try {
				mqttClient.unsubscribe(subTopic);
				accordionSubs.getPanes().remove(titledPane);
			} catch(MqttException e) {
				showError("There was an MQTT error.", e.getMessage());
			}
		});

		hBoxButtonBar.getChildren().add(buttonClear);
		hBoxButtonBar.getChildren().add(buttonUnsubscribe);

		// Finish
		vBoxRoot.getChildren().add(hBoxButtonBar);
		VBox.setMargin(hBoxButtonBar, new Insets(0, 0, 8, 0));

		vBoxRoot.getChildren().add(tableView);
		VBox.setVgrow(tableView, Priority.ALWAYS);

		accordionSubs.getPanes().add(titledPane);

		try {
			mqttClient.subscribe(subTopic, (topic, msg) -> {
				byte[] payload = msg.getPayload();

				Timestamp stamp = new Timestamp(System.currentTimeMillis());
				Date date = new Date(stamp.getTime());
				SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss");

				tableView.getItems().add(
						new Message(
								stamp.getTime(),
								sdf.format(date),
								new String(payload, StandardCharsets.UTF_8)
						)
				);

				tableView.refresh();
				tableView.sort();
			});
		} catch(MqttException e) {
			showError("There was an MQTT error.", e.getMessage());
		}
	}

	public void connect() {
		Connection activeConnection = workspace.getConnections().get(listConnections.getSelectionModel().getSelectedIndex());

		try {
			stopMqtt();
			enableUi();

			mqttClient = new MqttClient(activeConnection.getHostname(), activeConnection.getClientId(), new MemoryPersistence());

			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(activeConnection.isAutoReconnect());
			options.setCleanSession(activeConnection.isCleanSession());
			options.setConnectionTimeout(10);
			options.setKeepAliveInterval(activeConnection.getKeepAlive());

			String username = activeConnection.getUsername();
			String password = activeConnection.getPassword();

			if(!username.isEmpty() && !username.isBlank() && !password.isEmpty() && !password.isBlank()) {
				options.setUserName(activeConnection.getUsername());
				options.setPassword(activeConnection.getPassword().toCharArray());
			}

			mqttClient.connect(options);
		} catch(MqttException e) {
			disableUi();
			showError("There was an MQTT error.", e.getMessage());
			e.printStackTrace();
		}

		labelName.setText(activeConnection.getName());
	}

	public void refreshConnections() {
		listConnections.getItems().clear();
		workspace.getConnections().forEach(connection -> listConnections.getItems().add(connection.getName()));
		listConnections.refresh();
	}

	public void disableUi() {
		fieldSendTopic.setDisable(true);
		fieldReceiveTopic.setDisable(true);
		buttonSend.setDisable(true);
		areaMessage.setDisable(true);
		buttonSubscribe.setDisable(true);
	}

	public void enableUi() {
		fieldSendTopic.setDisable(false);
		fieldReceiveTopic.setDisable(false);
		buttonSend.setDisable(false);
		areaMessage.setDisable(false);
		buttonSubscribe.setDisable(false);
	}

	public void stopMqtt() {
		try {
			if(mqttClient == null) {
				return;
			}

			if(mqttClient.isConnected()) {
				mqttClient.disconnect();
			}

			accordionSubs.getPanes().clear();
			mqttClient.close();
		} catch(MqttException e) {
			e.printStackTrace();
		}
	}

	public void showError(String headerText, String contentText) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("ZapIt Error");
		alert.setHeaderText(headerText);
		alert.setContentText(contentText);
		alert.showAndWait();
	}
}