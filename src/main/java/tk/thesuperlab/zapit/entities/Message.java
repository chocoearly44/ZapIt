package tk.thesuperlab.zapit.entities;

import lombok.Getter;

@Getter
public class Message {
	private final Long timestamp;
	private final String time;
	private final String message;

	public Message(Long timestamp, String time, String message) {
		this.timestamp = timestamp;
		this.time = time;
		this.message = message;
	}
}
