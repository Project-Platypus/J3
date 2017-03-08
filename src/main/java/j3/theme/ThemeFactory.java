package j3.theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import j3.theme.impl.LightTheme;

public class ThemeFactory {
	
	public static ThemeProvider DEFAULT = new LightTheme();
	
	private static ThemeFactory INSTANCE;

	private final ServiceLoader<ThemeProvider> loader;
	
	private ThemeFactory() {
		super();
		
		loader = ServiceLoader.load(ThemeProvider.class);
	}
	
	public static ThemeFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ThemeFactory();
		}
		
		return INSTANCE;
	}
	
	public List<String> getNames() {
		return getProviders().stream().map(theme -> theme.getName()).collect(Collectors.toList());
	}
	
	public List<ThemeProvider> getProviders() {
		List<ThemeProvider> providers = new ArrayList<>();
		loader.forEach(provider -> providers.add(provider));
		return providers;
	}
	
	public ThemeProvider getProvider(String name) {
		for (ThemeProvider provider : getProviders()) {
			if (provider.getName().equals(name)) {
				return provider;
			}
		}
		
		return DEFAULT;
	}
	
	public List<String> getStylesheets(String name) {
		ThemeProvider provider = getProvider(name);
		
		if (provider == null) {
			return Collections.emptyList();
		} else {
			return provider.getStylesheets();
		}
	}

}
