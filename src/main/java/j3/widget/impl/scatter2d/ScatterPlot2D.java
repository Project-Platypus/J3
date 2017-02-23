package j3.widget.impl.scatter2d;

import j3.Axis;
import j3.Canvas;
import j3.colormap.Colormap;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.widget.SerializableWidget;
import j3.widget.TargetableWidget;
import j3.widget.TitledWidget;
import j3.widget.Widget;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ScatterPlot2D extends TitledWidget<ScatterPlot2D> implements SerializableWidget, TargetableWidget {

	private Axis2D plot;
	
	private ScatterPoints2D scatter;
	
	private ObservableList<Widget<?>> dependencies = FXCollections.observableArrayList();
	
	public ScatterPlot2D() {
		super();
	}

	@Override
	public ScatterPlot2D getNode() {
		return this;
	}

	@Override
	public void initialize(Canvas canvas) {
		Pane container = new Pane();
		container.setPadding(new Insets(5, 5, 5, 5));
		
		// this is needed to prevent the height growing (need to fix the root cause)
		container.setPrefHeight(300);
		
		DataFrame table = (DataFrame)canvas.getPropertyRegistry().get("data").getValue();
		
		plot = new Axis2D();
		scatter = new ScatterPoints2D(plot, table);
		
		setContent(plot);
		setTitle("2D Scatter Plot");
		
		ObjectProperty<Axis> xAxis = canvas.getPropertyRegistry().get("xAxis");
		ObjectProperty<Axis> yAxis = canvas.getPropertyRegistry().get("yAxis");
		ObjectProperty<Axis> colorAxis = canvas.getPropertyRegistry().get("colorAxis");
		ObjectProperty<Axis> sizeAxis = canvas.getPropertyRegistry().get("sizeAxis");
		ObjectProperty<Axis> visibilityAxis = canvas.getPropertyRegistry().get("visibilityAxis");
		ObjectProperty<Colormap> colormap = canvas.getPropertyRegistry().get("colormap");
		ObjectProperty<Instance> selectedInstance = canvas.getPropertyRegistry().get("selectedInstance");
		
		scatter.xAxisProperty().bind(xAxis);
		scatter.yAxisProperty().bind(yAxis);
		scatter.colorAxisProperty().bind(colorAxis);
		scatter.sizeAxisProperty().bind(sizeAxis);
		scatter.visibilityAxisProperty().bind(visibilityAxis);
		scatter.colormapProperty().bind(colormap);
		scatter.selectedInstanceProperty().bindBidirectional(selectedInstance);
	}
	
	@Override
	public void onActivate(Canvas canvas) {
		super.onActivate(canvas);

		canvas.setBoxSelectionHandler(event -> {
			setLayoutX(canvas.getSelectionBox().getX());
			setLayoutY(canvas.getSelectionBox().getY());
			
			pane.setPrefWidth(Math.max(300, canvas.getSelectionBox().getWidth()));
			pane.setPrefHeight(Math.max(200, canvas.getSelectionBox().getHeight()));

			canvas.add(this);

			event.consume();
		});
	}

	@Override
	public void onRemove(Canvas canvas) {
		scatter.xAxisProperty().unbind();
		scatter.yAxisProperty().unbind();
		scatter.colorAxisProperty().unbind();
		scatter.sizeAxisProperty().unbind();
		scatter.visibilityAxisProperty().unbind();
		scatter.colormapProperty().unbind();
		scatter.selectedInstanceProperty().unbind();
	}
	
	public void update() {
		scatter.update();
	}

	@Override
	public Element saveState(Canvas canvas) {
		Element element = DocumentHelper.createElement("scatter2d");
		
		saveStateInternal(element);
		
		// store the node ids
		Element mapping = element.addElement("mapping");
		
		for (Node node : scatter.getPoints()) {
			Instance instance = (Instance)node.getUserData();
			
			Element map = mapping.addElement("map");
			map.addAttribute("instanceId", instance.getId().toString());
			map.addAttribute("nodeId", node.getId());
		}
		
		return element;
	}

	@Override
	public void restoreState(Element element, Canvas canvas) {
		restoreStateInternal(element);
		
		// update the node ids
		Map<UUID, UUID> cache = new HashMap<UUID, UUID>();
		Element mapping = element.element("mapping");
		
		for (Object obj : mapping.elements("map")) {
			Element map = (Element)obj;
			cache.put(UUID.fromString(map.attributeValue("instanceId")),
					UUID.fromString(map.attributeValue("nodeId")));
		}
		
		for (Node node : scatter.getPoints()) {
			Instance instance = (Instance)node.getUserData();
			node.setId(cache.get(instance.getId()).toString());
		}
	}
	
	@Override
	public ObservableList<Widget<?>> getDependencies() {
		return dependencies;
	}

}
