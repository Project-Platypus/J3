package j3.widget.impl.parallel;

import j3.Axis;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class VerticalAxis extends Region {

	public static final int HALF_WIDTH = 2;

	public static final int VGAP = 10;

	private final Axis axis;

	Line line;

	Text label;

	private double[] tickPositions;

	private List<Line> tickLines;

	private List<Text> tickTexts;

	public VerticalAxis(Axis axis) {
		super();
		this.axis = axis;

		tickLines = new ArrayList<>();
		tickTexts = new ArrayList<>();

		line = new Line(HALF_WIDTH, 0, HALF_WIDTH, 100);
		line.getStyleClass().add("j3-parallel-line");

		label = new Text(axis.getLabel());
		label.getStyleClass().add("j3-parallel-label");

		tickPositions = axis.getTickPositions();
		String[] tickLabels = axis.getTickLabels();

		for (int i = 0; i < tickPositions.length; i++) {
			Line tickLine = new Line(0, 0, 2 * HALF_WIDTH, 0);
			tickLine.getStyleClass().add("j3-parallel-tick-line");

			Text tickLabel = new Text(tickLabels[i]);
			tickLabel.getStyleClass().add("j3-parallel-tick-label");
			tickLabel.setLayoutX(-tickLabel.prefWidth(30));

			tickLines.add(tickLine);
			tickTexts.add(tickLabel);
		}

		getChildren().addAll(line, label);
		getChildren().addAll(tickLines);
		getChildren().addAll(tickTexts);

		prefHeightProperty().addListener((observable, oldValue, newValue) -> update());

		setManaged(false);
		setPickOnBounds(false);
	}

	public Axis getAxis() {
		return axis;
	}

	public void update() {
		double height = getPrefHeight();
		double labelWidth = label.prefWidth(30);
		double labelHeight = label.prefHeight(labelWidth);

		label.setLayoutY(labelHeight);
		label.setLayoutX(-labelWidth / 2.0);

		labelHeight += VGAP;

		line.setStartY(labelHeight);
		line.setEndY(height);

		for (int i = 0; i < tickPositions.length; i++) {
			tickLines.get(i).setTranslateY(labelHeight + (height - labelHeight) * tickPositions[i]);
			tickTexts.get(i).setTranslateY(
					labelHeight + (height - labelHeight) * tickPositions[i] + tickTexts.get(i).prefHeight(100) / 4.0);
		}
	}

}
