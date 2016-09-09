package j3.widgets.threed;

import j3.Axis;
import j3.colormap.Colormap;
import j3.dataframe.Attribute;
import j3.dataframe.DataFrame;
import j3.transition.DiffuseColorTransition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Group;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.util.Duration;

public class ScatterPoints extends Region implements Plot3D {
	
	private Axis3D axisBox;

	private DataFrame table;

	private ObjectProperty<Axis> xAxis = new ObjectPropertyBase<Axis>() {

		{
			addListener((observable, oldValue, newValue) -> {
				updateXYZAxes();
			});
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
		}

		@Override
		public String getName() {
			return "X";
		}

	};

	public void setXAxis(Axis axis) {
		xAxis.set(axis);
	}

	public Axis getXAxis() {
		return xAxis.get();
	}

	public ObjectProperty<Axis> xAxisProperty() {
		return xAxis;
	}

	private ObjectProperty<Axis> yAxis = new ObjectPropertyBase<Axis>() {
		
		{
			addListener((observable, oldValue, newValue) -> {
				updateXYZAxes();
			});
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
		}

		@Override
		public String getName() {
			return "Y";
		}

	};

	public void setYAxis(Axis axis) {
		yAxis.set(axis);
	}

	public Axis getYAxis() {
		return yAxis.get();
	}

	public ObjectProperty<Axis> yAxisProperty() {
		return yAxis;
	}

	private ObjectProperty<Axis> zAxis = new ObjectPropertyBase<Axis>() {
		
		{
			addListener((observable, oldValue, newValue) -> {
				updateXYZAxes();
			});
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
		}

		@Override
		public String getName() {
			return "Z";
		}

	};

	public void setZAxis(Axis axis) {
		zAxis.set(axis);
	}

	public Axis getZAxis() {
		return zAxis.get();
	}

	public ObjectProperty<Axis> zAxisProperty() {
		return zAxis;
	}
	
	private ObjectProperty<Axis> colorAxis = new ObjectPropertyBase<Axis>() {
		
		{
			addListener((observable, oldValue, newValue) -> {
				updateColorAxis();
			});
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
		}

		@Override
		public String getName() {
			return "Color";
		}

	};

	public void setColorAxis(Axis axis) {
		colorAxis.set(axis);
	}

	public Axis getColorAxis() {
		return colorAxis.get();
	}

	public ObjectProperty<Axis> colorAxisProperty() {
		return colorAxis;
	}
	
	private ObjectProperty<Axis> sizeAxis = new ObjectPropertyBase<Axis>() {
		
		{
			addListener((observable, oldValue, newValue) -> {
				updateSizeAxis();
			});
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
		}

		@Override
		public String getName() {
			return "Size";
		}

	};

	public void setSizeAxis(Axis axis) {
		sizeAxis.set(axis);
	}

	public Axis getSizeAxis() {
		return sizeAxis.get();
	}

	public ObjectProperty<Axis> sizeAxisProperty() {
		return sizeAxis;
	}

	private ObjectProperty<Colormap> colormap = new ObjectPropertyBase<Colormap>() {

		@Override
		protected void invalidated() {
			for (int i = 0; i < materials.size(); i++) {
				Color newColor = colormap.get().map(i / (double)(materials.size()-1));
				DiffuseColorTransition transition = new DiffuseColorTransition(new Duration(1000), materials.get(i), newColor);
				transition.play();
			}
		}

		@Override
		public Object getBean() {
			return ScatterPoints.this;
		}

		@Override
		public String getName() {
			return "colormap";
		}

	};

	public void setColormap(Colormap colormap) {
		this.colormap.set(colormap);
	}

	public Colormap getColormap() {
		return colormap.get();
	}

	public ObjectProperty<Colormap> colormapProperty() {
		return colormap;
	}

	private Group pointGroup;

	private List<Shape3D> points;

	private List<PhongMaterial> materials;

	public ScatterPoints(Axis3D axisBox, DataFrame table) {
		super();
		this.axisBox = axisBox;
		this.table = table;

		points = new ArrayList<Shape3D>();

		materials = new ArrayList<PhongMaterial>();

		for (int i = 0; i < 256; i++) {
			materials.add(new PhongMaterial());
		}

		pointGroup = new Group();
		pointGroup.getStyleClass().addAll("j3-points");

		axisBox.xAxisProperty().bind(xAxisProperty());
		axisBox.yAxisProperty().bind(yAxisProperty());
		axisBox.zAxisProperty().bind(zAxisProperty());

		for (int i = 0; i < table.instanceCount(); i++) {
			Shape3D box = new Box(10, 10, 10);
			box.setUserData(table.getInstance(i));
			points.add(box);
		}

		pointGroup.getChildren().addAll(points);
		axisBox.setPlotContents(pointGroup);
	}

	protected List<Shape3D> getPoints() {
		return points;
	}
	
	private class PointTransition extends Transition {
		
		private double[] startX = new double[points.size()];
		
		private double[] startY = new double[points.size()];
		
		private double[] startZ = new double[points.size()];
		
		public PointTransition() {
			super();
			
			setCycleDuration(new Duration(1000));
			setCycleCount(1);
			
			for (int i = 0; i < points.size(); i++) {
				startX[i] = points.get(i).getTranslateX();
				startY[i] = points.get(i).getTranslateY();
				startZ[i] = points.get(i).getTranslateZ();
			}
		}

		@Override
		protected void interpolate(double frac) {
			Axis xAxis = getXAxis();
			Axis yAxis = getYAxis();
			Axis zAxis = getZAxis();
			
			for (int i = 0; i < points.size(); i++) {
				Shape3D box = points.get(i);

				double endX = axisBox.getSide(0).getSize() * ((xAxis == null ? 0.0 : map(xAxis, xAxis.getColumn(), i)) - 0.5);
				double endY = axisBox.getSide(1).getSize() * (0.5 - (yAxis == null ? 0.0 : map(yAxis, yAxis.getColumn(), i)));
				double endZ = axisBox.getSide(2).getSize() * ((zAxis == null ? 0.0 : map(zAxis, zAxis.getColumn(), i)) - 0.5);
				
				box.setTranslateX(startX[i] + (endX - startX[i]) * frac);
				box.setTranslateY(startY[i] + (endY - startY[i]) * frac);
				box.setTranslateZ(startZ[i] + (endZ - startZ[i]) * frac);
			}
		}
		
	}
	
	private double map(Axis axis, Attribute<?> column, int row) {
		return axis.map(table.getInstance(row).get(column));
	}
	
	private class ColorTransition extends Transition {
		
		private int[] start = new int[points.size()];
		
		private int[] end = new int[points.size()];
		
		public ColorTransition() {
			super();
			
			setCycleDuration(new Duration(1000));
			setCycleCount(1);
			
			Axis colorAxis = getColorAxis();
			
			for (int i = 0; i < points.size(); i++) {
				int startIndex = materials.indexOf((PhongMaterial)points.get(i).getMaterial());
				int endIndex = (int)(255*(colorAxis == null ? 0.0 : map(colorAxis, colorAxis.getColumn(), i)));
				
				if (startIndex < 0) {
					startIndex = 0;
				}
				
				start[i] = startIndex;
				end[i] = endIndex;
			}
		}

		@Override
		protected void interpolate(double frac) {
			for (int i = 0; i < points.size(); i++) {
				Shape3D box = points.get(i);
				box.setMaterial(materials.get(start[i] + (int)((end[i] - start[i]) * frac)));
			}
		}
		
	}
	
	private class SizeTransition extends Transition {
		
		private double[] start = new double[points.size()];
		
		private double[] end = new double[points.size()];
		
		public SizeTransition() {
			super();
			
			setCycleDuration(new Duration(1000));
			setCycleCount(1);
			
			Axis sizeAxis = getSizeAxis();
			
			for (int i = 0; i < points.size(); i++) {
				start[i] = points.get(i).getScaleX();
				end[i] = (sizeAxis == null ? 0.8 : map(sizeAxis, sizeAxis.getColumn(), i)) + 0.2;
			}
		}

		@Override
		protected void interpolate(double frac) {
			for (int i = 0; i < points.size(); i++) {
				Shape3D box = points.get(i);
				box.setScaleX(start[i] + (end[i] - start[i]) * frac);
				box.setScaleY(start[i] + (end[i] - start[i]) * frac);
				box.setScaleZ(start[i] + (end[i] - start[i]) * frac);
			}
		}
		
	}

	public void updateXYZAxes() {
		if (points.isEmpty()) {
			return;
		}
		
		PointTransition pt = new PointTransition();
		pt.play();
	}
	
	public void updateColorAxis() {
		if (points.isEmpty()) {
			return;
		}
		
		ColorTransition ct = new ColorTransition();
		ct.play();
	}
	
	public void updateSizeAxis() {
		if (points.isEmpty()) {
			return;
		}
		
		SizeTransition st = new SizeTransition();
		st.play();
	}

	@Override
	public List<ObjectProperty<Axis>> getAxisProperties() {
		return Arrays.asList(
				xAxis,
				yAxis,
				zAxis,
				colorAxis,
				sizeAxis);
	}

}