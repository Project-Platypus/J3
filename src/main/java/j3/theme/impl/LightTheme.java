package j3.theme.impl;

import java.util.Arrays;
import java.util.List;

import j3.theme.ThemeProvider;

public class LightTheme implements ThemeProvider {

	@Override
	public String getName() {
		return "Light";
	}

	@Override
	public String getDescription() {
		return "Light theme (default)";
	}

	@Override
	public List<String> getStylesheets() {
		return Arrays.asList(LightTheme.class.getResource("j3-light.css").toExternalForm());
	}

}
