package j3.theme.impl;

import java.util.Arrays;
import java.util.List;

import j3.theme.ThemeProvider;

public class DarkTheme implements ThemeProvider {

	@Override
	public String getName() {
		return "Dark";
	}

	@Override
	public String getDescription() {
		return "Dark theme";
	}

	@Override
	public List<String> getStylesheets() {
		return Arrays.asList(DarkTheme.class.getResource("j3-dark.css").toExternalForm());
	}

}
