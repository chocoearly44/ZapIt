package tk.thesuperlab.zapit.pages;

import atlantafx.base.theme.Styles;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tk.thesuperlab.nitron.managers.AboutManager;
import tk.thesuperlab.nitron.managers.NotificationManager;
import tk.thesuperlab.nitron.utils.FxUtils;
import tk.thesuperlab.zapit.ZapitApplication;
import tk.thesuperlab.zapit.config.ZapitWorkspace;
import tk.thesuperlab.zapit.entities.Connection;
import tk.thesuperlab.zapit.pages.homepage.ConnectionView;
import tk.thesuperlab.zapit.popups.EditConnection;
import tk.thesuperlab.zapit.popups.Settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

import static tk.thesuperlab.zapit.ZapitApplication.workspace;
import static tk.thesuperlab.zapit.ZapitApplication.workspaceConfigurator;

public class Homepage {
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

		listConnections.setOnMouseClicked(event -> {
			if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
				try {
					buttonConnectOnAction();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@FXML
	public void buttonAddServerOnAction() {
		FxUtils.openDialog(
				new EditConnection(this, null),
				ZapitApplication.class.getResource("popups/edit-connection.fxml"),
				null,
				ZapitApplication.class.getResource("icon.png"),
				"Add connection",
				Modality.NONE
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
		ConnectionView connectionController = new ConnectionView(connection, stackPane);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("homepage/connection-view.fxml"));
		fxmlLoader.setControllerFactory(controllerClass -> connectionController);

		Tab tab = new Tab(connection.getName());
		tab.setContent(fxmlLoader.load());
		tab.setClosable(true);
		tab.setOnClosed(event -> {
			activeConnections.remove(connection);
			connectionController.disconnect();
		});

		tabs.getTabs().add(tab);
		tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
	}

	@FXML
	public void menuConnectionsEditOnAction() {
		if(listConnections.getSelectionModel().getSelectedItem() == null) {
			return;
		}

		Connection connection = workspace.getConnections().get(listConnections.getSelectionModel().getSelectedIndex());
		FxUtils.openDialog(
				new EditConnection(this, connection),
				ZapitApplication.class.getResource("popups/edit-connection.fxml"),
				null,
				ZapitApplication.class.getResource("icon.png"),
				"Add connection",
				Modality.NONE
		);
	}

	@FXML
	public void menuConnectionsRemoveOnAction() {
		String selectedItem = listConnections.getSelectionModel().getSelectedItem();

		if(selectedItem != null) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure that you want to delete " + selectedItem + " ?", ButtonType.NO, ButtonType.YES);
			alert.showAndWait();

			if(alert.getResult() == ButtonType.YES) {
				int index = listConnections.getSelectionModel().getSelectedIndex();
				workspace.getConnections().remove(index);
				workspaceConfigurator.saveConfig("config.json", workspace);
				refreshConnections();
			}
		}
	}

	@FXML
	public void menuPreferencesOnAction() {
		FxUtils.openDialog(
				Settings.class,
				ZapitApplication.class.getResource("popups/settings.fxml"),
				null,
				ZapitApplication.class.getResource("icon.png"),
				"ZapIt Settings",
				Modality.NONE
		);
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
		workspace = workspaceConfigurator.getConfig("config.json", ZapitWorkspace.class);
		listConnections.getItems().clear();
		workspace.getConnections().forEach(connection -> listConnections.getItems().add(connection.getName()));
		listConnections.refresh();
	}
}