package tk.thesuperlab.zapit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tk.thesuperlab.nitron.config.AppDetails;
import tk.thesuperlab.nitron.config.GlobalConfigurator;
import tk.thesuperlab.nitron.config.WorkspaceConfig;
import tk.thesuperlab.nitron.config.WorkspaceConfigurator;
import tk.thesuperlab.zapit.config.ZapitWorkspace;

import java.util.Locale;
import java.util.ResourceBundle;

public class ZapitApplication extends Application {
	public static GlobalConfigurator<WorkspaceConfig> globalConfigurator;
	public static WorkspaceConfig globalConfig;

	public static WorkspaceConfigurator workspaceConfigurator;
	public static ZapitWorkspace workspace;

	public static void main(String[] args) {
		launch();
	}

	@Override
	public void start(Stage stage) throws Exception {
		// Load config and workspace
		globalConfigurator = GlobalConfigurator.getInstance(new AppDetails("ZapIt", "zapit"), WorkspaceConfig.class);
		globalConfig = globalConfigurator.loadConfig();

		workspaceConfigurator = WorkspaceConfigurator.getInstance(globalConfig);
		workspace = workspaceConfigurator.getConfig("config.json", ZapitWorkspace.class);

		// Setup JavaFX
		Application.setUserAgentStylesheet(
				workspaceConfigurator.getConfig("config.json", ZapitWorkspace.class)
						.getTheme()
						.getAtlantaTheme()
						.getUserAgentStylesheet()
		);

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("pages/homepage.fxml"));
		fxmlLoader.setResources(ResourceBundle.getBundle("locales.messages", new Locale("en", "en")));

		Scene scene = new Scene(fxmlLoader.load(), 1400, 800);
		stage.setTitle("ZapIt");
		stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
		stage.setScene(scene);
		stage.show();
	}
}