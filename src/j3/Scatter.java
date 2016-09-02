package j3;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;

public class Scatter extends Region {

	private Axis3D axisBox;
	
	private Table table;
	
	private Group pointGroup;
	
	private List<Shape3D> points;
	
	public Scatter(Axis3D axisBox, Table table) {
		super();
		this.axisBox = axisBox;
		this.table = table;
		
		points = new ArrayList<Shape3D>();
		
    	List<PhongMaterial> materials = new ArrayList<PhongMaterial>();
    	
    	for (int i = 0; i < 256; i++) {
    		PhongMaterial material = new PhongMaterial();
    		material.setDiffuseColor(Color.hsb(360*i/(double)256, 1.0, 0.8));
    		materials.add(material);
    	}
    	
    	pointGroup = new Group();
    	pointGroup.getStyleClass().addAll("j3-points");
    	
//    	Random random = new Random();
//    	axisBox.getAxis(0).scale(Arrays.asList(0.0, 1.0));
//    	axisBox.getAxis(1).scale(Arrays.asList(0.0, 1.0));
//    	axisBox.getAxis(2).scale(Arrays.asList(0.0, 1.0));
//        
//        for (int i = 0; i < 10000; i++) {
//        	Shape3D box = new Box(10, 10, 10);
//        	box.setMaterial(materials.get(random.nextInt(256)));
//        	
//        	box.setTranslateX(axisBox.getSide(0).getSize() * (axisBox.getAxis(0).map(random.nextDouble()) - 0.5));
//        	box.setTranslateY(axisBox.getSide(1).getSize() * (axisBox.getAxis(1).map(random.nextDouble()) - 0.5));
//        	box.setTranslateZ(axisBox.getSide(2).getSize() * (axisBox.getAxis(2).map(random.nextDouble()) - 0.5));
//
//        	points.add(box);
//        }
        
      axisBox.getAxis(0).scale(Arrays.asList(table.floatColumn(0).min(), table.floatColumn(0).max()));
      axisBox.getAxis(1).scale(Arrays.asList(table.floatColumn(1).min(), table.floatColumn(1).max()));
      axisBox.getAxis(2).scale(Arrays.asList(table.floatColumn(2).min(), table.floatColumn(2).max()));
      
      axisBox.getAxis(0).setLabel(table.column(0).name());
      axisBox.getAxis(1).setLabel(table.column(1).name());
      axisBox.getAxis(2).setLabel(table.column(2).name());
        
        for (int i = 0; i < table.rowCount(); i++) {
        	Shape3D box = new Box(10, 10, 10);
        	box.setMaterial(materials.get((int)(255*((table.floatColumn(3).get(i) - table.floatColumn(3).min())/table.floatColumn(3).range()))));
        	
        	box.setTranslateX(axisBox.getSide(0).getSize() * (axisBox.getAxis(0).map(table.floatColumn(0).get(i)) - 0.5));
        	box.setTranslateY(axisBox.getSide(1).getSize() * (axisBox.getAxis(1).map(table.floatColumn(1).get(i)) - 0.5));
        	box.setTranslateZ(axisBox.getSide(2).getSize() * (axisBox.getAxis(2).map(table.floatColumn(2).get(i)) - 0.5));

        	//Tooltip.install(box, new Tooltip("Hovering over box " + i));
        	
        	points.add(box);
        }
        
        pointGroup.getChildren().addAll(points);
        axisBox.setPlotContents(pointGroup);
	}
	
	protected List<Shape3D> getPoints() {
		return points;
	}

	public void initializePlot() {
		
	}
	
}
