package j3;

import j3.dataframe.Attribute;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;

public class RealAxis extends Axis {
	
	private int nticks = 5;
	
	private double minValue;
	
	private double maxValue;
	
	private String[] tickLabels;
	
	private double[] tickPositions;
	
	private NumberFormat format = new DecimalFormat("0.0");

	public RealAxis(Attribute<?> column) {
		super(column);
		
		computeTicks();
	}

	public double getMinValue() {
		return minValue;
	}

	public double getMaxValue() {
		return maxValue;
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
	public double map(Object value) {
		if (value instanceof Number) {
			return (((Number)value).doubleValue() - minValue) / (maxValue - minValue);
		} else {
			throw new IllegalArgumentException("RealAxis can only map numbers");
		}
	}
	
	@Override
	public void scale(Collection<?> values) {
		double min = Double.POSITIVE_INFINITY;
		double max = Double.NEGATIVE_INFINITY;
		
		for (Object value : values) {
			if (value instanceof Number) {
				min = Math.min(min, ((Number)value).doubleValue());
				max = Math.max(max, ((Number)value).doubleValue());
			} else {
				throw new IllegalArgumentException("RealAxis can only scale with numbers");
			}
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
