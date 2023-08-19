package tk.thesuperlab.zapit;

import atlantafx.base.theme.Styles;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import tk.thesuperlab.nitron.managers.NotificationManager;
import tk.thesuperlab.zapit.entities.Connection;
import tk.thesuperlab.zapit.entities.Message;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class ConnectionController implements Initializable {
	private final Connection connection;
	private final StackPane parentPane;

	private MqttClient mqttClient;

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

	public ConnectionController(Connection connection, StackPane parentPane) {
		this.connection = connection;
		this.parentPane = parentPane;

		try {
			this.mqttClient = new MqttClient(connection.getHostname(), connection.getClientId(), new MemoryPersistence());
		} catch(MqttException e) {
			showError(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(connection.isAutoReconnect());
			options.setCleanSession(connection.isCleanSession());
			options.setConnectionTimeout(10);
			options.setKeepAliveInterval(connection.getKeepAlive());

			String username = connection.getUsername();
			String password = connection.getPassword();

			if(!username.isEmpty() && !username.isBlank() && !password.isEmpty() && !password.isBlank()) {
				options.setUserName(connection.getUsername());
				options.setPassword(connection.getPassword().toCharArray());
			}

			mqttClient.connect(options);
			NotificationManager.showNotification("Successfully connected to the server", Styles.SUCCESS, true, parentPane);
		} catch(MqttException e) {
			disableUi();
			showError(e);
		}

		labelName.setText(connection.getName());
		enableUi();
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
			showError(e);
		}
	}

	public void subscribeTopic(String subTopic) {
		VBox vBoxRoot = new VBox();
		TitledPane titledPane = new TitledPane(subTopic, vBoxRoot);
		titledPane.setAnimated(false);

		// Table view
		TableView<Message> tableView = new TableView<>();
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
				showError(e);
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
			showError(e);
		}
	}

	public void disconnect() {
		stopMqtt();
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
			showError(e);
		}
	}

	private void showError(Exception e) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("ZapIt Error");
		alert.setHeaderText("There was an MQTT error.");
		alert.setContentText(e.getMessage());

		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);
		e.printStackTrace(System.err);

		TextArea textArea = new TextArea(stringWriter.toString());
		textArea.setEditable(false);
		textArea.setWrapText(false);
		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane content = new GridPane();
		content.setMaxWidth(Double.MAX_VALUE);
		content.add(new Label("Full stacktrace:"), 0, 0);
		content.add(textArea, 0, 1);

		alert.getDialogPane().setExpandableContent(content);
		alert.initOwner(labelName.getScene().getWindow());
		alert.show();
	}
}
