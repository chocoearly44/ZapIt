package tk.thesuperlab.zapit.utils.filesystem;

import tk.thesuperlab.zapit.entities.Connection;
import tk.thesuperlab.zapit.entities.Workspace;
import tk.thesuperlab.zapit.utils.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class UnixStorage implements StorageUtils {
	@Override
	public void initialise() {
		File storageFolder = getStorageFolder();
		storageFolder.mkdir();
	}

	@Override
	public File getStorageFolder() {
		return new File(System.getProperty("user.home") + "/.zapit");
	}

	@Override
	public File createDefaultWorkspace() {
		File workspace = new File(getStorageFolder() + "/default.zwp");

		if(!workspace.exists()) {
			try {
				workspace.createNewFile();
				Workspace defaultWorkspace = new Workspace(new ArrayList<Connection>());

				FileOutputStream fout = new FileOutputStream(workspace);
				ObjectOutputStream oos = new ObjectOutputStream(fout);

				oos.writeObject(defaultWorkspace);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

		return workspace;
	}

	@Override
	public void saveWorkspace(Workspace workspace) {
		try {
			FileOutputStream fout = new FileOutputStream(getStorageFolder() + "/default.zwp");
			ObjectOutputStream oos = new ObjectOutputStream(fout);

			oos.writeObject(workspace);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
