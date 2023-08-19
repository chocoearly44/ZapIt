package tk.thesuperlab.zapit;

import atlantafx.base.theme.PrimerDark;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import tk.thesuperlab.zapit.entities.Config;
import tk.thesuperlab.zapit.entities.Workspace;
import tk.thesuperlab.zapit.utils.StorageUtils;
import tk.thesuperlab.zapit.utils.filesystem.UnixStorage;
import tk.thesuperlab.zapit.utils.filesystem.WindowsStorage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ZapitApplication extends Application {
	public static StorageUtils storageUtils;
	public static Workspace workspace;
	public static Config config;

	public static void main(String[] args) {
		launch();
	}

	public static void loadConfig() {
		config = storageUtils.getConfig();
		workspace = storageUtils.getWorkspace();
	}

	@Override
	public void start(Stage stage) throws IOException {
		// Initialise filesystem
		String osName = System.getProperty("os.name").toLowerCase();

		if(osName.startsWith("win")) {
			storageUtils = new WindowsStorage();
		} else if(osName.startsWith("linux")) {
			storageUtils = new UnixStorage();
		}

		storageUtils.initialise();

		// Load files
		loadConfig();

		// Setup JavaFX
		Application.setUserAgentStylesheet(new PrimerDark().getUserAgentStylesheet());

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("zapit-view.fxml"));
		fxmlLoader.setResources(ResourceBundle.getBundle("locales.messages", new Locale("en", "en")));

		Scene scene = new Scene(fxmlLoader.load(), 1500, 800);
		stage.setTitle("ZapIt");
		stage.getIcons().add(new Image(ZapitController.class.getResourceAsStream("icon.png")));
		stage.setScene(scene);
		stage.show();
	}
}