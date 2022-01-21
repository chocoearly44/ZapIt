package tk.thesuperlab.zapit.entities;

import java.io.Serializable;
import java.util.ArrayList;

public class Workspace implements Serializable {
	private ArrayList<Connection> connections;

	public Workspace(ArrayList<Connection> connections) {
		this.connections = connections;
	}

	public ArrayList<Connection> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<Connection> connections) {
		this.connections = connections;
	}
}
