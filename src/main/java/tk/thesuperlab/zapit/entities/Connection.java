package tk.thesuperlab.zapit.entities;

import java.io.Serializable;

public class Connection implements Serializable {
	private String name;
	private String hostname;
	private String clientId;
	private int keepAlive;
	private boolean cleanSession;
	private boolean autoReconnect;
	private String username;
	private String password;

	public Connection(String name, String hostname, String clientId, int keepAlive, boolean cleanSession, boolean autoReconnect, String username, String password) {
		this.name = name;
		this.hostname = hostname;
		this.clientId = clientId;
		this.keepAlive = keepAlive;
		this.cleanSession = cleanSession;
		this.autoReconnect = autoReconnect;
		this.username = username;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(int keepAlive) {
		this.keepAlive = keepAlive;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public boolean isAutoReconnect() {
		return autoReconnect;
	}

	public void setAutoReconnect(boolean autoReconnect) {
		this.autoReconnect = autoReconnect;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
