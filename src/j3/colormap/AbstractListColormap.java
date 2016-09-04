package j3.colormap;

import javafx.scene.paint.Color;

public abstract class AbstractListColormap implements Colormap {
	
	private double[][] colorList;
	
	public abstract double[][] getColorList();
	
	@Override
	public Color map(double value) {
		if (colorList == null) {
			colorList = getColorList();
		}
		
		int index = (int)(value*(colorList.length-1));
		double[] color = colorList[index];
		
		if (color.length == 3) {
			return Color.rgb((int)(color[0]*255), (int)(color[1]*255), (int)(color[2]*255));
		} else if (color.length == 4) {
			return Color.rgb((int)(color[0]*255), (int)(color[1]*255), (int)(color[2]*255), color[3]);
		} else {
			throw new IllegalArgumentException("color list can only contain arrays with 3 or 4 values");
		}
	}

}
