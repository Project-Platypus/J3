package j3;

import j3.dataframe.Attribute;

import java.util.Collection;

public abstract class Axis {
	
	private Attribute<?> column;
	
	private String label;

	public Axis(Attribute<?> column) {
		super();
		this.column = column;
		
		if (column != null) {
			this.label = column.getName();
		}
	}
	
	public Attribute<?> getColumn() {
		return column;
	}

	public void setColumn(Attribute<?> column) {
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
	
	public abstract void scale(Collection<?> values);

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
