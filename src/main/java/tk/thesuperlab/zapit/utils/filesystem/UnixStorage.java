package tk.thesuperlab.zapit.utils.filesystem;

import tk.thesuperlab.zapit.entities.Config;
import tk.thesuperlab.zapit.entities.Connection;
import tk.thesuperlab.zapit.entities.Workspace;
import tk.thesuperlab.zapit.utils.StorageUtils;

import java.io.*;
import java.util.ArrayList;

public class UnixStorage implements StorageUtils {
	@Override
	public void initialise() {
		// Setup ZapIt folder
		File storageFolder = getStorageFolder();
		storageFolder.mkdir();

		// Initialize files
		File workspace = new File(getStorageFolder() + "/default.zwp");
		File config = new File(getStorageFolder() + "/config.zcf");

		if(!config.exists()) {
			try {
				workspace.createNewFile();
				config.createNewFile();

				FileOutputStream fos;
				ObjectOutputStream oos;

				// Setup default workspace
				Workspace defaultWorkspace = new Workspace(new ArrayList<Connection>());
				fos = new FileOutputStream(workspace);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(defaultWorkspace);

				// Setup default config
				Config defaultConfig = new Config(getStorageFolder() + "/default.zwp", false);
				fos = new FileOutputStream(config);
				oos = new ObjectOutputStream(fos);
				oos.writeObject(defaultConfig);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public File getStorageFolder() {
		return new File(System.getProperty("user.home") + "/.zapit");
	}

	@Override
	public Workspace getWorkspace() {
		try {
			FileInputStream fis = new FileInputStream(getConfig().getWorkspacePath());
			ObjectInputStream ois = new ObjectInputStream(fis);

			return (Workspace) ois.readObject();
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void saveWorkspace(Workspace workspace) {
		try {
			FileOutputStream fout = new FileOutputStream(getConfig().getWorkspacePath());
			ObjectOutputStream oos = new ObjectOutputStream(fout);

			oos.writeObject(workspace);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Config getConfig() {
		try {
			FileInputStream fis = new FileInputStream(getStorageFolder() + "/config.zcf");
			ObjectInputStream ois = new ObjectInputStream(fis);

			return (Config) ois.readObject();
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void saveConfig(Config config) {
		try {
			FileOutputStream fout = new FileOutputStream(getStorageFolder() + "/config.zcf");
			ObjectOutputStream oos = new ObjectOutputStream(fout);

			oos.writeObject(config);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
