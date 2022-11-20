package j3.colormap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

public class ColormapFactory {

	private static ColormapFactory INSTANCE;

	private final Map<String, Colormap> cache;

	private final ServiceLoader<ColormapProvider> loader;

	private ColormapFactory() {
		super();

		cache = new HashMap<String, Colormap>();
		loader = ServiceLoader.load(ColormapProvider.class);
	}

	public static ColormapFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ColormapFactory();
		}

		return INSTANCE;
	}

	public List<String> getNames() {
		List<String> names = new ArrayList<String>();
		loader.forEach(provider -> names.addAll(provider.getNames()));
		return names;
	}

	public Colormap getColormap(String name) {
		Colormap colormap = cache.get(name);

		if (colormap == null) {
			Iterator<ColormapProvider> iterator = loader.iterator();

			while (iterator.hasNext()) {
				ColormapProvider provider = iterator.next();

				if (provider.getNames().contains(name)) {
					colormap = provider.getColormap(name);
					break;
				}
			}
		}

		return colormap;
	}

	public List<ColormapProvider> getProviders() {
		List<ColormapProvider> providers = new ArrayList<ColormapProvider>();
		loader.forEach(provider -> providers.add(provider));
		return providers;
	}

}
