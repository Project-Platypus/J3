package j3.colormap.impl;

import java.util.Arrays;
import java.util.List;

import j3.colormap.Colormap;
import j3.colormap.ColormapProvider;
import j3.colormap.LinearSequentialColormap;

public class SequentialColormaps implements ColormapProvider {

	@Override
	public String getCategory() {
		return "Sequential";
	}

	@Override
	public List<String> getNames() {
		return Arrays.asList(
				"winter",
				"spring",
				"summer",
				"autumn",
				"cool",
				"hot",
				"gray",
				"copper",
				"bone");
	}

	@Override
	public Colormap getColormap(String name) {
		switch (name) {
		case "winter":
			return new LinearSequentialColormap(
					new double[][] {{ 0., 0., 0. }, { 1.0, 0.0, 0.0 }},
					new double[][] {{ 0., 0., 0. }, { 1.0, 1.0, 1.0 }},
					new double[][] {{ 0., 1., 1. }, { 1.0, 0.5, 0.5 }});
		case "spring":
			return new LinearSequentialColormap(
					new double[][] {{ 0., 1., 1. }, { 1.0, 1.0, 1.0 }},
					new double[][] {{ 0., 0., 0. }, { 1.0, 1.0, 1.0}},
					new double[][] {{ 0., 1., 1. }, { 1.0, 0.0, 0.0}});
		case "summer":
			return new LinearSequentialColormap(
					new double[][] {{ 0., 0., 0. }, { 1.0, 1.0, 1.0 }},
					new double[][] {{ 0., 0.5, 0.5 }, { 1.0, 1.0, 1.0 }},
					new double[][] {{ 0., 0.4, 0.4 }, { 1.0, 0.4, 0.4 }});
		case "autumn":
			return new LinearSequentialColormap(
					new double[][] {{ 0.0, 1.0, 1.0 }, { 1.0, 1.0, 1.0 }},
					new double[][] {{ 0.0, 0.0, 0.0 }, { 1.0, 1.0, 1.0 }},
					new double[][] {{ 0.0, 0.0, 0.0 }, { 1.0, 0.0, 0.0 }});
		case "cool":
			return new LinearSequentialColormap(
					new double[][] {{ 0., 0., 0. }, { 1.0, 1.0, 1.0 }},
					new double[][] {{ 0., 1., 1. }, { 1.0, 0.,  0. }},
					new double[][] {{ 0., 1., 1. }, { 1.0, 1., 1. }});
		case "hot":
			return new LinearSequentialColormap(
					new double[][] {{0., 0.0416, 0.0416}, {0.365079, 1.000000, 1.000000}, {1.0, 1.0, 1.0}},
					new double[][] {{0., 0., 0.}, {0.365079, 0.000000, 0.000000}, {0.746032, 1.000000, 1.000000}, {1.0, 1.0, 1.0}},
					new double[][] {{0., 0., 0.}, {0.746032, 0.000000, 0.000000}, {1.0, 1.0, 1.0}});
		case "bone":
			return new LinearSequentialColormap(
					new double[][] {{0., 0., 0.}, {0.746032, 0.652778, 0.652778}, {1.0, 1.0, 1.0}},
					new double[][] {{0., 0., 0.}, {0.365079, 0.319444, 0.319444}, {0.746032, 0.777778, 0.777778},  {1.0, 1.0, 1.0}},
					new double[][] {{0., 0., 0.}, {0.365079, 0.444444, 0.444444}, {1.0, 1.0, 1.0}});
		case "copper":
			return new LinearSequentialColormap(
					new double[][] {{0., 0., 0.}, {0.809524, 1.000000, 1.000000}, {1.0, 1.0, 1.0}},
					new double[][] {{0., 0., 0.}, {1.0, 0.7812, 0.7812}},
					new double[][] {{0., 0., 0.}, {1.0, 0.4975, 0.4975}});
		case "gray":
			return new LinearSequentialColormap(
					new double[][] {{0., 0, 0}, {1., 1, 1}},
					new double[][] {{0., 0, 0}, {1., 1, 1}},
					new double[][] {{0., 0, 0}, {1., 1, 1}});
		default:
			throw new IllegalArgumentException(getClass().getName() + " does not support colormap '" + name + "'");
		}
	}

}
