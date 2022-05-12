package tk.thesuperlab.zapit.utils;

import tk.thesuperlab.zapit.entities.Config;
import tk.thesuperlab.zapit.entities.Workspace;

import java.io.File;

public interface StorageUtils {
	void initialise();

	File getStorageFolder();

	Workspace getWorkspace();

	void saveWorkspace(Workspace workspace);

	Config getConfig();

	void saveConfig(Config config);
}
