package j3.colormap.impl;

import java.util.Arrays;
import java.util.List;

import j3.colormap.Colormap;
import j3.colormap.ColormapProvider;
import j3.colormap.LinearSequentialColormap;

public class MiscellaneousColormaps implements ColormapProvider {

	@Override
	public String getCategory() {
		return "Miscellaneous";
	}

	@Override
	public List<String> getNames() {
		return Arrays.asList(new String[] {
				"rainbow",
				"hsv",
				"jet",
				"stoplight"
		});
	}

	@Override
	public Colormap getColormap(String name) {
		switch (name) {
		case "rainbow":
			return new RainbowColormap();
		case "hsv":
			return new HSVColormap();
		case "jet":
			return new LinearSequentialColormap(
					name,
					new double[][] {{0, 0, 0}, {0.35, 0, 0}, {0.66, 1, 1}, {0.89, 1, 1}, {1, 0.5, 0.5}},
					new double[][] {{0., 0, 0}, {0.125, 0, 0}, {0.375, 1, 1}, {0.64, 1, 1}, {0.91, 0, 0}, {1, 0, 0}},
					new double[][] {{0., 0.5, 0.5}, {0.11, 1, 1}, {0.34, 1, 1}, {0.65, 0, 0}, {1, 0, 0}});
		case "stoplight":
			return new HSVColormap("stoplight", 0.0, 120.0);
		default:
			throw new IllegalArgumentException(getClass().getName() + " does not support colormap '" + name + "'");
		}
	}

}
