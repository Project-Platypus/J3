package j3.widget.impl.scatter;

import j3.Axis;
import j3.colormap.Colormap;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
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
	
	private ObjectProperty<Axis> visibilityAxis = new ObjectPropertyBase<Axis>() {
		
		{
			addListener((observable, oldValue, newValue) -> {
				updateVisibilityAxis();
			});
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
		}

		@Override
		public String getName() {
			return "Visibility";
		}

	};

	public void setVisibilityAxis(Axis axis) {
		visibilityAxis.set(axis);
	}

	public Axis getVisibilityAxis() {
		return visibilityAxis.get();
	}

	public ObjectProperty<Axis> visibilityAxisProperty() {
		return visibilityAxis;
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
	
	private ObjectProperty<Instance> selectedInstance = new ObjectPropertyBase<Instance>() {
		
//		private Instance oldInstance = null;
//		
//		private Material oldMaterial = null;
//		
//		private final Material selectedMaterial = new PhongMaterial(Color.WHITE);

		@Override
		protected void invalidated() {
//			for (Shape3D shape : points) {
//				if (shape.getUserData() == oldInstance) {
//					shape.setMaterial(oldMaterial);
//				}
//			}
//			
//			for (Shape3D shape : points) {
//				if (shape.getUserData() == selectedInstance.get()) {
//					oldMaterial = shape.getMaterial();
//					shape.setMaterial(selectedMaterial);
//				}
//			}
//			
//			oldInstance = selectedInstance.get();
			
			updateColorAxis();
		}

		@Override
		public Object getBean() {
			return ScatterPoints.this;
		}

		@Override
		public String getName() {
			return "selectedInstance";
		}

	};

	public void setSelectedInstance(Instance instance) {
		selectedInstance.set(instance);
	}

	public Instance getSelectedInstance() {
		return selectedInstance.get();
	}

	public ObjectProperty<Instance> selectedInstanceProperty() {
		return selectedInstance;
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
			Instance instance = table.getInstance(i);
			box.setUserData(instance);
			box.setOnMouseClicked(event -> setSelectedInstance(instance));
			points.add(box);
		}

		pointGroup.getChildren().addAll(points);
		getChildren().addAll(pointGroup);
		axisBox.setPlotContents(this);
		
		setManaged(false);
		setPickOnBounds(false);
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

				double endX = axisBox.getSide(0).getSize() * ((xAxis == null ? 0.0 : map(xAxis, i)) - 0.5);
				double endY = axisBox.getSide(1).getSize() * (0.5 - (yAxis == null ? 0.0 : map(yAxis, i)));
				double endZ = axisBox.getSide(2).getSize() * ((zAxis == null ? 0.0 : map(zAxis, i)) - 0.5);
				
				box.setTranslateX(startX[i] + (endX - startX[i]) * frac);
				box.setTranslateY(startY[i] + (endY - startY[i]) * frac);
				box.setTranslateZ(startZ[i] + (endZ - startZ[i]) * frac);
			}
		}
		
	}
	
	private double map(Axis axis, int row) {
		return axis.map(table.getInstance(row));
	}
	
	private int[] start;
	
	private int[] end;
	
	private class ColorTransition extends Transition {
		
		public ColorTransition() {
			super();
			
			setCycleDuration(new Duration(1000));
			setCycleCount(1);
			
			Axis colorAxis = getColorAxis();
		
			if (start == null) {
				start = new int[points.size()];
				end = new int[points.size()];
				
				for (int i = 0; i < points.size(); i++) {
					int startIndex = materials.indexOf((PhongMaterial)points.get(i).getMaterial());
				
					if (startIndex < 0) {
						startIndex = 0;
					}
					
					start[i] = startIndex;
					end[i] = startIndex;
				}
			}
			
			for (int i = 0; i < points.size(); i++) {
				int startIndex = end[i];
				int endIndex = (int)(255*(colorAxis == null ? 0.0 : map(colorAxis, i)));
				
				if (selectedInstance.get() != null) {
					if (points.get(i).getUserData() != selectedInstance.get()) {
						endIndex = -1; 
					}
				}
				
				start[i] = startIndex;
				end[i] = endIndex;
			}
		}

		@Override
		protected void interpolate(double frac) {
			for (int i = 0; i < points.size(); i++) {
				Shape3D box = points.get(i);
				
				if (frac == 0.0) {
					box.setMaterial(start[i] == -1 ? new PhongMaterial(Color.GRAY) : materials.get(start[i]));
				} else if (frac == 1.0) {
					box.setMaterial(end[i] == -1 ? new PhongMaterial(Color.GRAY) : materials.get(end[i]));
				} else {
					Color originalColor = start[i] == -1 ? Color.GRAY : materials.get(start[i]).getDiffuseColor(); 
					Color newColor = originalColor.interpolate(
							end[i] == -1 ? Color.GRAY : materials.get(end[i]).getDiffuseColor(),
							frac);
							
					box.setMaterial(new PhongMaterial(newColor));
				}
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
				end[i] = (sizeAxis == null ? 0.8 : map(sizeAxis, i)) + 0.2;
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
	
	public void updateVisibilityAxis() {
		if (points.isEmpty()) {
			return;
		}

		Axis visibilityAxis = getVisibilityAxis();
		
		for (int i = 0; i < points.size(); i++) {
			points.get(i).setVisible(visibilityAxis == null ? true : map(visibilityAxis, i) > 0.5);
		}
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
	
	public void update() {
		updateVisibilityAxis();
	}

}
