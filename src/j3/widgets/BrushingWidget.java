package j3.widgets;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.RangeSlider;

import j3.Axis;
import j3.Canvas;
import j3.CategoryAxis;
import j3.RealAxis;
import j3.Selector;
import j3.dataframe.BooleanAttribute;
import j3.dataframe.DataFrame;
import j3.widgets.threed.ScatterPoints;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class BrushingWidget extends TitledWidget<BrushingWidget> {
	
	public static final BooleanAttribute BRUSHING_ATTRIBUTE = new BooleanAttribute("J3_BRUSHING");
	
	private final Map<RangeSlider, Axis> sliderMap;

	public BrushingWidget() {
		super();
		
		sliderMap = new HashMap<>();
	}

	@Override
	public BrushingWidget getNode() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivate(Canvas canvas) {
		super.onActivate(canvas);
		
		canvas.setSingleClickHandler(event -> {
			VBox container = new VBox();
			container.setPadding(new Insets(5, 5, 5, 5));
			
			DataFrame table = (DataFrame)canvas.getSharedData().get("data").getValue();
			List<Axis> options = ((ObjectProperty<List<Axis>>)canvas.getSharedData().get("axes")).get();
			
			CategoryAxis brushingAxis = new CategoryAxis(BRUSHING_ATTRIBUTE);
			brushingAxis.scale(Arrays.asList(false, true));
			
			ChangeListener<Number> changeListener = (observable, oldValue, newValue) -> {
				table.getInstances().forEach(instance -> {
					boolean isWithinBounds = true;
					
					for (RangeSlider slider : sliderMap.keySet()) {
						Axis axis = sliderMap.get(slider);
						Double value = (Double)instance.get(axis.getColumn());
						
						if (value < slider.getLowValue() || value > slider.getHighValue()) {
							isWithinBounds = false;
							break;
						}
					}
					
					instance.set(BRUSHING_ATTRIBUTE, isWithinBounds);
				});
				
				Selector.on(canvas).select("*").get(ScatterPoints.class).forEach(node -> node.update());
			};
			
			// call change listener to initialize the attribute values before setting the visibility axis
			changeListener.changed(null, null, null);
			((ObjectProperty<Axis>)canvas.getSharedData().get("visibilityAxis")).set(brushingAxis);
			
			for (Axis axis : options) {
				if (axis instanceof RealAxis) {
					RealAxis realAxis = (RealAxis)axis;
					RangeSlider slider = new RangeSlider(realAxis.getMinValue(), realAxis.getMaxValue(),
							realAxis.getMinValue(), realAxis.getMaxValue());
					slider.setShowTickLabels(true);
					slider.setShowTickLabels(true);
					slider.setMinorTickCount(5);
					slider.lowValueProperty().addListener(changeListener);
					slider.highValueProperty().addListener(changeListener);
					sliderMap.put(slider, axis);
					
					Text label = new Text(axis.getLabel());
					VBox.setMargin(label, new Insets(5, 0, 0, 0));
					
					container.getChildren().addAll(label, slider);
				}
			}
			
			FlowPane buttonPane = new FlowPane();
			buttonPane.setAlignment(Pos.CENTER);
			
			Button resetButton = new Button("Reset");
			resetButton.setOnAction(e -> {
				for (RangeSlider slider : sliderMap.keySet()) {
					slider.setLowValue(slider.getMin());
					slider.setHighValue(slider.getMax());
				}
			});
			
			buttonPane.getChildren().add(resetButton);
			container.getChildren().add(buttonPane);
			
			setContent(container);
			setTitle("Brushing");
			
			Point2D point = new Point2D(event.getScreenX(), event.getScreenY());
			point = canvas.screenToLocal(point);
			
			setLayoutX(point.getX());
			setLayoutY(point.getY());
			
			canvas.add(this);
			
			event.consume();
		});
	}

	@Override
	public void onRemove(Canvas canvas) {
		for (RangeSlider slider : sliderMap.keySet()) {
			slider.setLowValue(slider.getMin());
			slider.setHighValue(slider.getMax());
		}
	}
	
	
}
