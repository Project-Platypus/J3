package j3.widget;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

public class WidgetFactory {
	
	private static WidgetFactory INSTANCE;

	private final ServiceLoader<WidgetProvider> loader;
	
	private WidgetFactory() {
		super();
		
		loader = ServiceLoader.load(WidgetProvider.class);
	}
	
	public static WidgetFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new WidgetFactory();
		}
		
		return INSTANCE;
	}
	
	public Set<String> getCategories() {
		Set<String> categories = new LinkedHashSet<>();
		loader.forEach(provider -> categories.add(provider.getCategory()));
		return categories;
	}
	
	public List<WidgetProvider> getProviders() {
		List<WidgetProvider> providers = new ArrayList<>();
		loader.forEach(provider -> providers.add(provider));
		return providers;
	}
	
	public List<WidgetProvider> getProviders(String category) {
		List<WidgetProvider> providers = new ArrayList<>();
		
		loader.forEach(provider -> {
			if (provider.getCategory().equals(category)) {
				providers.add(provider);
			}
		});
		
		return providers;
	}

}
