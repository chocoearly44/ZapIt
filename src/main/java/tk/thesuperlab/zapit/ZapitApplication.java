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
import tk.thesuperlab.zapit.entities.Workspace;
import tk.thesuperlab.zapit.utils.StorageUtils;
import tk.thesuperlab.zapit.utils.filesystem.UnixStorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ZapitApplication extends Application {
	public static StorageUtils storageUtils;
	public static Workspace workspace;

	private Stage stage;

	@Override
	public void start(Stage stage) throws IOException {
		// Initialise filesystem
		switch(System.getProperty("os.name").toLowerCase()) {
			case "windows":
				//TODO: Windows support
				break;
			case "linux":
				storageUtils = new UnixStorage();
				break;
		}

		storageUtils.initialise();

		// Read default workspace
		File defaultWorkspace = storageUtils.createDefaultWorkspace();

		try {
			FileInputStream fis = new FileInputStream(defaultWorkspace);
			ObjectInputStream ois = new ObjectInputStream(fis);
			workspace = (Workspace) ois.readObject();
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		// Setup JavaFX
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("zapit-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
		stage.setTitle("ZapIt");
		stage.getIcons().add(new Image(ZapitController.class.getResourceAsStream("icon.png")));
		stage.setScene(scene);

		JMetro jMetro = new JMetro(Style.DARK);
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