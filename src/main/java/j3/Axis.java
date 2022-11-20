package j3;

import j3.dataframe.Attribute;
import j3.dataframe.Instance;

import java.util.Collection;

public abstract class Axis {

	private Attribute<?> attribute;

	private String label;

	public Axis(Attribute<?> column) {
		super();
		this.attribute = column;

		if (column != null) {
			this.label = column.getName();
		}
	}

	public Attribute<?> getAttribute() {
		return attribute;
	}

	public void setAttribute(Attribute<?> column) {
		this.attribute = column;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public abstract String[] getTickLabels();

	/**
	 * Returns the tick positions on the axis. Positions are scaled between 0 and 1.
	 * 
	 * @return
	 */
	public abstract double[] getTickPositions();

	public abstract void scale(Collection<?> values);

	/**
	 * Maps from a value to a number between 0 and 1. This number will subsequently
	 * be scaled by the plotting routines to fit the plot.
	 * 
	 * @param value
	 * @return
	 */
	public abstract double map(Object value);

	/**
	 * Shorthand notation for calling
	 * {@code axis.map(instance.get(axis.getAttribute()))}.
	 * 
	 * @param instance
	 * @return
	 */
	public double map(Instance instance) {
		return map(instance.get(attribute));
	}

	public String toString() {
		return getLabel();
	}

}
