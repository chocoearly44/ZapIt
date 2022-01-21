package tk.thesuperlab.zapit.entities;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Message {
	private final SimpleLongProperty timestamp;
	private final SimpleStringProperty time;
	private final SimpleStringProperty message;

	public Message(Long timestamp, String time, String message) {
		this.timestamp = new SimpleLongProperty(timestamp);
		this.time = new SimpleStringProperty(time);
		this.message = new SimpleStringProperty(message);
	}

	public long getTimestamp() {
		return timestamp.get();
	}

	public String getTime() {
		return time.get();
	}

	public String getMessage() {
		return message.get();
	}
}
