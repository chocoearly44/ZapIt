package tk.thesuperlab.zapit;

import atlantafx.base.theme.Styles;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tk.thesuperlab.nitron.managers.AboutManager;
import tk.thesuperlab.nitron.managers.NotificationManager;
import tk.thesuperlab.nitron.managers.PreferencesManager;
import tk.thesuperlab.nitron.utils.FxUtils;
import tk.thesuperlab.zapit.entities.Connection;
import tk.thesuperlab.zapit.popups.ConnectionPopup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static tk.thesuperlab.zapit.ZapitApplication.storageUtils;
import static tk.thesuperlab.zapit.ZapitApplication.workspace;

public class ZapitController {
	private final ArrayList<Connection> activeConnections = new ArrayList<>();

	@FXML
	private StackPane stackPane;
	@FXML
	private ListView<String> listConnections;
	@FXML
	private TabPane tabs;

	@FXML
	public void initialize() {
		refreshConnections();

		/*menuNewWorkspace.setOnAction(event -> {});
		menuOpenWorkspace.setOnAction(event -> {});
		menuSaveWorkspace.setOnAction(event -> {});
		menuSaveWorkspaceAs.setOnAction(event -> {});*/
	}

	@FXML
	public void buttonAddServerOnAction() {
		FxUtils.openDialog(
				new ConnectionPopup(this),
				getClass().getResource("popups/connection.fxml"),
				null,
				ZapitController.class.getResource("icon.png"),
				"Add connection"
		);
	}

	@FXML
	public void buttonConnectOnAction() throws IOException {
		if(listConnections.getSelectionModel().getSelectedItem() == null) {
			return;
		}

		Connection connection = workspace.getConnections().get(listConnections.getSelectionModel().getSelectedIndex());
		if(activeConnections.contains(connection)) {
			NotificationManager.showNotification("Already connected", Styles.DANGER, true, stackPane);
			return;
		}

		activeConnections.add(connection);
		ConnectionController connectionController = new ConnectionController(connection, stackPane);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("connection-view.fxml"));
		fxmlLoader.setControllerFactory(controllerClass -> connectionController);

		Tab tab = new Tab(connection.getName());
		tab.setContent(fxmlLoader.load());
		tab.setClosable(true);
		tab.setOnClosed(event -> connectionController.disconnect());

		tabs.getTabs().add(tab);
		tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
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
				int index = listConnections.getSelectionModel().getSelectedIndex();
				workspace.getConnections().remove(index);
				storageUtils.saveWorkspace(workspace);
				refreshConnections();
			}
		}
	}

	@FXML
	public void menuPreferencesOnAction() throws IOException {
		/*FXMLLoader aboutLoader = new FXMLLoader(getClass().getResource("popups/settings.fxml"));
		Parent aboutScene = aboutLoader.load();

		Stage aboutStage = new Stage();
		aboutStage.initModality(Modality.APPLICATION_MODAL);
		aboutStage.setTitle("ZapIt settings");
		aboutStage.getIcons().add(new Image(ZapitController.class.getResourceAsStream("icon.png")));
		aboutStage.setScene(new Scene(aboutScene));
		aboutStage.setOnHidden(windowEvent -> {
			ZapitApplication.loadConfig();
			refreshConnections();
		});

		JMetro jMetroSettings = config.isDarkMode() ? new JMetro(Style.DARK) : new JMetro(Style.LIGHT);
		jMetroSettings.setParent(aboutScene);

		aboutStage.show();*/

		PreferencesManager.openWindow(new File("C:\\Users\\JurijFortuna\\zapit_workspace"));
	}

	@FXML
	public void menuExitOnAction() {
		Stage app = (Stage) listConnections.getScene().getWindow();
		app.close();
	}

	@FXML
	public void menuAboutOnAction() {
		AboutManager.openWindow(
				ZapitApplication.class.getResource("icon.png"),
				ResourceBundle.getBundle("locales.messages", new Locale("en", "en"))
		);
	}

	public void refreshConnections() {
		listConnections.getItems().clear();
		workspace.getConnections().forEach(connection -> listConnections.getItems().add(connection.getName()));
		listConnections.refresh();
	}
}