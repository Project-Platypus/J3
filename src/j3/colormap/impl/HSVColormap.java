package j3.colormap.impl;

import j3.colormap.Colormap;
import javafx.scene.paint.Color;

public class HSVColormap implements Colormap {
	
	private double beginAngle;
	
	private double endAngle;
	
	private double saturation = 1.0;
	
	private double brightness = 0.8;
	
	public HSVColormap() {
		this(0.0, 360.0);
	}
	
	public HSVColormap(double beginAngle, double endAngle) {
		super();
		this.beginAngle = beginAngle;
		this.endAngle = endAngle;
	}

	@Override
	public Color map(double value) {
		return Color.hsb(beginAngle + (endAngle-beginAngle)*value, saturation, brightness);
	}

}
