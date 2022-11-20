package j3.widget.impl;

import j3.Axis;
import j3.Canvas;
import j3.CategoryAxis;
import j3.RealAxis;
import j3.Selector;
import j3.dataframe.BooleanAttribute;
import j3.dataframe.DataFrame;
import j3.widget.TitledWidget;
import j3.widget.impl.parallel.ParallelCoordinates;
import j3.widget.impl.scatter.ScatterPoints;
import j3.widget.impl.scatter2d.ScatterPoints2D;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import org.controlsfx.control.RangeSlider;

public class BrushingWidget extends TitledWidget<BrushingWidget> {

	public static final BooleanAttribute BRUSHING_ATTRIBUTE = new BooleanAttribute("J3_BRUSHING");

	private final Map<RangeSlider, Axis> sliderMap;

	private final Map<CheckBox, Axis> checkBoxMap;

	public BrushingWidget() {
		super();

		sliderMap = new HashMap<>();
		checkBoxMap = new HashMap<>();
	}

	@Override
	public BrushingWidget getNode() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivate(Canvas canvas) {
		canvas.setSingleClickHandler(event -> {
			VBox container = new VBox();
			container.setPadding(new Insets(5, 5, 5, 5));

			DataFrame table = (DataFrame) canvas.getPropertyRegistry().get("data").getValue();
			List<Axis> options = (List<Axis>) canvas.getPropertyRegistry().get("axes").getValue();

			CategoryAxis brushingAxis = new CategoryAxis(BRUSHING_ATTRIBUTE);
			brushingAxis.scale(Arrays.asList(false, true));

			ChangeListener<? super Object> changeListener = (observable, oldValue, newValue) -> {
				table.getInstances().forEach(instance -> {
					boolean isWithinBounds = true;

					for (RangeSlider slider : sliderMap.keySet()) {
						Axis axis = sliderMap.get(slider);
						Double value = (Double) instance.get(axis.getAttribute());

						if (value < slider.getLowValue() || value > slider.getHighValue()) {
							isWithinBounds = false;
							break;
						}
					}

					if (isWithinBounds) {
						for (CheckBox checkbox : checkBoxMap.keySet()) {
							Axis axis = checkBoxMap.get(checkbox);
							Object value = instance.get(axis.getAttribute());

							if (!checkbox.isSelected() && value.equals(checkbox.getUserData())) {
								isWithinBounds = false;
								break;
							}
						}
					}

					instance.set(BRUSHING_ATTRIBUTE, isWithinBounds);
				});

				Selector.on(canvas).get(ScatterPoints.class).forEach(node -> node.update());
				Selector.on(canvas).get(ParallelCoordinates.class).forEach(node -> node.update());
				Selector.on(canvas).get(ScatterPoints2D.class).forEach(node -> node.update());
			};

			// call change listener to initialize the attribute values before setting the
			// visibility axis
			changeListener.changed(null, null, null);
			canvas.getPropertyRegistry().get("visibilityAxis").set(brushingAxis);

			for (Axis axis : options) {
				if (axis instanceof RealAxis) {
					RealAxis realAxis = (RealAxis) axis;
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
				} else if (axis instanceof CategoryAxis) {
					CategoryAxis categoryAxis = (CategoryAxis) axis;
					VBox buttonContainer = new VBox();
					buttonContainer.setPadding(new Insets(0, 0, 0, 25));

					for (Object category : categoryAxis.getCategories()) {
						CheckBox checkbox = new CheckBox();
						checkbox.setText(category.toString());
						checkbox.setSelected(true);
						checkbox.setUserData(category);
						checkbox.selectedProperty().addListener(changeListener);
						buttonContainer.getChildren().add(checkbox);
						checkBoxMap.put(checkbox, categoryAxis);
					}

					Text label = new Text(axis.getLabel());
					VBox.setMargin(label, new Insets(5, 0, 0, 0));

					container.getChildren().addAll(label, buttonContainer);
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

				for (CheckBox checkbox : checkBoxMap.keySet()) {
					checkbox.setSelected(true);
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

		for (CheckBox checkbox : checkBoxMap.keySet()) {
			checkbox.setSelected(true);
		}
	}

}
