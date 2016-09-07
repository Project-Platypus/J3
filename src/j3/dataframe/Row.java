package j3.dataframe;

public class Row {
	
	DataFrame dataFrame;
	
	int index;
	
	public Row(DataFrame dataFrame, int index) {
		super();
		this.dataFrame = dataFrame;
		this.index = index;
	}
	
	public void set(int column, Object value) {
		dataFrame.getColumn(column).set(index, value);
	}
	
	public <T> T get(int column) {
		return (T)dataFrame.getColumn(column).get(index);
	}

}
