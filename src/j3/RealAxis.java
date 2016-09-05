package j3;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;

public class RealAxis extends Axis<Number> {
	
	private int nticks = 5;
	
	private double minValue;
	
	private double maxValue;
	
	private String[] tickLabels;
	
	private double[] tickPositions;
	
	private NumberFormat format = new DecimalFormat("0.0");

	public RealAxis(int column, String label) {
		super(column, label);
		
		computeTicks();
	}

	@Override
	public String[] getTickLabels() {
		return tickLabels;
	}

	@Override
	public double[] getTickPositions() {
		return tickPositions;
	}

	@Override
	public double map(Number value) {
		return (value.doubleValue() - minValue) / (maxValue - minValue);
	}
	
	@Override
	public void scale(List<? extends Number> values) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		
		for (Number value : values) {
			min = Math.min(min, value.doubleValue());
			max = Math.max(max, value.doubleValue());
		}
		
		if ((minValue != min) || (maxValue != max)) {
			minValue = min;
			maxValue = max;
			computeTicks();
		}
	}
	
	public void computeTicks() {
		tickLabels = new String[nticks];
		tickPositions = new double[nticks];
		
		for (int i = 0; i < nticks; i++) {
			double position = i/(nticks-1.0);
			double value = minValue + (maxValue-minValue)*position;
			
			tickLabels[i] = format.format(value);
			tickPositions[i] = position;
		}
	}

}
