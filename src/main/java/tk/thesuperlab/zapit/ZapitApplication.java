package tk.thesuperlab.zapit;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import tk.thesuperlab.zapit.entities.Config;
import tk.thesuperlab.zapit.entities.Workspace;
import tk.thesuperlab.zapit.utils.StorageUtils;
import tk.thesuperlab.zapit.utils.filesystem.UnixStorage;
import tk.thesuperlab.zapit.utils.filesystem.WindowsStorage;

import java.io.IOException;

public class ZapitApplication extends Application {
	public static StorageUtils storageUtils;
	public static Workspace workspace;
	public static Config config;

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
		config = storageUtils.getConfig();
		workspace = storageUtils.getWorkspace();

		// Setup JavaFX
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("zapit-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
		stage.setTitle("ZapIt");
		stage.getIcons().add(new Image(ZapitController.class.getResourceAsStream("icon.png")));
		stage.setScene(scene);

		JMetro jMetro;
		if(config.isDarkMode()) {
			jMetro = new JMetro(Style.DARK);
		} else {
			jMetro = new JMetro(Style.LIGHT);
		}

		jMetro.setScene(scene);

		ZapitController controller = fxmlLoader.getController();
		stage.show();

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				controller.stopMqtt();
			}
		});
	}

	public static void main(String[] args) {
		launch();
	}
}