package j3.colormap;

import javafx.scene.paint.Color;

/**
 * A colormap provides a mapping between a numeric value on the range
 * {@code [0, 1]} to a color.
 */
public interface Colormap {
	
	public String getName();
	
	public Color map(double value);

}
