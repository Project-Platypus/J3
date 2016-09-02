package j3;

import java.util.List;

import javafx.event.EventTarget;

public abstract class Axis<T> {
	
	private int index;
	
	private String label;

	public Axis(int index, String label) {
		super();
		this.index = index;
		this.label = label;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getLabel() {
		return label;
	}
	
	public abstract String[] getTickLabels();
	
	public abstract double[] getTickPositions();
	
	public abstract void scale(List<? extends T> values);
	
	public abstract Domain getDomain();
	
	/**
	 * Maps from a value to a number between 0 and 1.  This number will subsequently be scaled by the plotting routines
	 * to fit the plot.
	 * 
	 * @param value
	 * @return
	 */
	public abstract double map(T value);

}
