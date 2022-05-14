package tk.thesuperlab.zapit.entities;

import java.io.Serializable;

public class Config implements Serializable {
	private String workspacePath;
	private boolean darkMode;

	public Config() {
	}

	public Config(String workspacePath, boolean darkMode) {
		this.workspacePath = workspacePath;
		this.darkMode = darkMode;
	}

	public String getWorkspacePath() {
		return workspacePath;
	}

	public void setWorkspacePath(String workspacePath) {
		this.workspacePath = workspacePath;
	}

	public boolean isDarkMode() {
		return darkMode;
	}

	public void setDarkMode(boolean darkMode) {
		this.darkMode = darkMode;
	}
}
