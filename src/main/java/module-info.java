module tk.thesuperlab.zapit {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.controlsfx.controls;
	requires org.jfxtras.styles.jmetro;
	requires org.eclipse.paho.client.mqttv3;
	requires java.desktop;
	requires java.sql;

	opens tk.thesuperlab.zapit to javafx.fxml;
	opens tk.thesuperlab.zapit.popups to javafx.fxml;
	opens tk.thesuperlab.zapit.entities to javafx.base;

	exports tk.thesuperlab.zapit;
	exports tk.thesuperlab.zapit.entities;
	exports tk.thesuperlab.zapit.utils;
}