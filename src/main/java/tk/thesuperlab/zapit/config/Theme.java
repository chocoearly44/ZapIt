package tk.thesuperlab.zapit.config;

import atlantafx.base.theme.*;
import lombok.Getter;

@Getter
public enum Theme {
	PRIMER_DARK(new PrimerDark()),
	PRIMER_LIGHT(new PrimerLight()),
	NORD_DARK(new NordDark()),
	NORD_LIGHT(new NordLight()),
	DRACULA(new Dracula()),
	CUPERTINO_DARK(new CupertinoDark()),
	CUPERTINO_LIGHT(new CupertinoLight());

	private final atlantafx.base.theme.Theme atlantaTheme;

	Theme(atlantafx.base.theme.Theme atlantaTheme) {
		this.atlantaTheme = atlantaTheme;
	}
}