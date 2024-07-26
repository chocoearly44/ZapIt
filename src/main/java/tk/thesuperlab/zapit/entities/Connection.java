package tk.thesuperlab.zapit.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Connection {
	private String name;
	private String hostname;
	private String clientId;
	private int keepAlive;
	private boolean cleanSession;
	private boolean autoReconnect;
	private String username;
	private String password;

	public Connection() {
	}

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
}
