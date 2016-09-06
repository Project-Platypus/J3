package j3;

import java.util.List;

public abstract class Axis {
	
	private int column;
	
	private String label;

	public Axis(int column, String label) {
		super();
		this.column = column;
		this.label = label;
	}
	
	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public abstract String[] getTickLabels();
	
	public abstract double[] getTickPositions();
	
	public abstract void scale(List<?> values);

	/**
	 * Maps from a value to a number between 0 and 1.  This number will subsequently be scaled by the plotting routines
	 * to fit the plot.
	 * 
	 * @param value
	 * @return
	 */
	public abstract double map(Object value);
	
	public String toString() {
		return getLabel();
	}

}
