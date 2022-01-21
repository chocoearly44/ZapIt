package tk.thesuperlab.zapit.utils;

import tk.thesuperlab.zapit.entities.Workspace;

import java.io.File;

public interface StorageUtils {
	void initialise();
	File getStorageFolder();
	File createDefaultWorkspace();
	void saveWorkspace(Workspace workspace);
}
