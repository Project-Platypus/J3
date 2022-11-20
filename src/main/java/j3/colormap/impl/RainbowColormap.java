package j3.colormap.impl;

import j3.colormap.Colormap;
import javafx.scene.paint.Color;

public class RainbowColormap implements Colormap {

	public static double clip(double value, double min, double max) {
		return value < min ? min : (value > max ? max : value);
	}

	@Override
	public Color map(double value) {
		double red = clip(Math.abs(2.0 * value - 0.5), 0.0, 1.0);
		double green = clip(Math.sin(value * Math.PI), 0.0, 1.0);
		double blue = clip(Math.cos(value * Math.PI / 2.0), 0.0, 1.0);

		return Color.rgb((int) (red * 255), (int) (green * 255), (int) (blue * 255));
	}

	@Override
	public String getName() {
		return "rainbow";
	}

}
