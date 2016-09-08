package j3;

import j3.dataframe.Attribute;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CategoryAxis extends Axis {
	
	private String[] tickLabels;
	
	private double[] tickPositions;
	
	private Set<Object> categories;
	
	private Map<Object, Double> positionMap;

	public CategoryAxis(Attribute<?> column) {
		super(column);
		
		categories = new LinkedHashSet<>();
		positionMap = new HashMap<>();
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
	public double map(Object value) {
		return positionMap.get(value);
	}
	
	@Override
	public void scale(Collection<?> values) {
		categories.clear();
		categories.addAll(values);
		computeTicks();
	}
	
	public void computeTicks() {
		int nticks = categories.size();
		int i = 0;
		
		tickLabels = new String[nticks];
		tickPositions = new double[nticks];
		
		for (Object category : categories) {
			double position = i/(nticks-1.0);
			
			positionMap.put(category, position);
			tickLabels[i] = category.toString();
			tickPositions[i] = position;
			
			i++;
		}
	}

}
