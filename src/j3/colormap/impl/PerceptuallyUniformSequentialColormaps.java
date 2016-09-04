package j3.colormap.impl;

import java.util.Arrays;
import java.util.List;

import j3.colormap.Colormap;
import j3.colormap.ColormapProvider;
import j3.colormap.ListColormap;

/**
 * Based on colormaps available in matplotlib.
 */
public class PerceptuallyUniformSequentialColormaps implements ColormapProvider {

	public PerceptuallyUniformSequentialColormaps() {
		super();
	}

	@Override
	public String getCategory() {
		return "Perceptually Uniform Sequential Colormaps";
	}

	@Override
	public List<String> getNames() {
		return Arrays.asList(new String[] {
				"viridis",
				"inferno",
				"plasma",
				"magma"
		});
	}

	@Override
	public Colormap getColormap(String name) {
		Colormap colormap = null;
		
		try {
			switch (name) {
			case "viridis":
				colormap = new ListColormap(PerceptuallyUniformSequentialColormaps.class.getResourceAsStream("viridis.cmap"));
				break;
			case "inferno":
				colormap = new ListColormap(PerceptuallyUniformSequentialColormaps.class.getResourceAsStream("inferno.cmap"));
				break;
			case "plasma":
				colormap = new ListColormap(PerceptuallyUniformSequentialColormaps.class.getResourceAsStream("plasma.cmap"));
				break;
			case "magma":
				colormap = new ListColormap(PerceptuallyUniformSequentialColormaps.class.getResourceAsStream("magma.cmap"));
				break;
			default:
				throw new IllegalArgumentException(getClass().getName() + " does not support colormap '" + name + "'");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException(getClass().getName() + " was unable to instantiate colormap '" + name + "'", e);
		}

		return colormap;
	}

}
