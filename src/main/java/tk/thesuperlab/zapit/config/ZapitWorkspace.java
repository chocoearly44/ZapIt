package tk.thesuperlab.zapit.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tk.thesuperlab.zapit.entities.Connection;

import java.util.ArrayList;

@Getter
@Setter
@ToString
public class ZapitWorkspace {
	private Theme theme;
	private ArrayList<Connection> connections;

	public ZapitWorkspace() {
		theme = Theme.PRIMER_DARK;
		connections = new ArrayList<>();
	}
}
