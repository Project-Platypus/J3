package j3.dataframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A data frame is similar to a matrix, except it stores columns of varying
 * types.
 */
public class DataFrame {
	
	private List<Column<?>> columns;
	
	private Map<String, Integer> nameToIndexMap;
	
	public DataFrame() {
		columns = new ArrayList<>();
		nameToIndexMap = new HashMap<>();
	}
	
	public int columnCount() {
		return columns.size();
	}
	
	public Column<?> getColumn(int index) {
		return columns.get(index);
	}
	
	public Column<?> getColumn(String name) {
		return columns.get(nameToIndexMap.get(name));
	}
	
	public void replaceColumn(int index, Column<?> column) {
		columns.set(index, column);
	}
	
	public void removeColumn(int index) {
		columns.remove(index);
	}
	
	public void removeColumn(String name) {
		columns.remove(nameToIndexMap.get(name));
	}
	
	public void appendColumn(Column<?> column) {
		columns.add(column);
	}
	
	public void insertColumn(int index, Column<?> column) {
		columns.add(index, column);
	}

}
