package j3.dataframe;

import java.util.List;
import java.util.OptionalDouble;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DoubleColumn extends BasicColumn<Double> {

	private boolean dirty;
	
	private Double min;
	
	private Double max;
	
	public DoubleColumn() {
		super(Double.class);
	}
	
	private DoubleColumn(List<Double> values) {
		super(Double.class);
		this.values = values;
	}
	
	@Override
	public Double get(int index) {
		return super.get(index);
	}

	@Override
	public void set(int index, Object value) {
		if (value instanceof Byte) {
			value = ((Byte) value).doubleValue();
		} else if (value instanceof Integer) {
			value = ((Integer) value).doubleValue();
		} else if (value instanceof Long) {
			value = ((Long) value).doubleValue();
		} else if (value instanceof Float) {
			value = ((Float) value).doubleValue();
		}
		
		super.set(index, value);
		dirty = true;
	}
	
	public void add(Double value) {
		values.add(value);
		dirty = true;
	}
	
	public Double min() {
		if (dirty) {
			OptionalDouble result = values.stream().mapToDouble(Double::doubleValue).min();
			
			if (result.isPresent()) {
				min = result.getAsDouble();
			} else {
				min = null;
			}
		}
		
		return min;
	}
	
	public Double max() {
		if (dirty) {
			OptionalDouble result = values.stream().mapToDouble(Double::doubleValue).max();
			
			if (result.isPresent()) {
				max = result.getAsDouble();
			} else {
				max = null;
			}
		}
		
		return max;
	}
	
	public DoubleColumn apply(Function<? super Double, ? extends Double> op) {
		return new DoubleColumn(values.stream().map(op).collect(Collectors.toList()));
	}
	
	public DoubleColumn rand(int size) {
		DoubleColumn dc = new DoubleColumn();
		
		for (int i = 0; i < size; i++) {
			dc.add(Math.random());
		}
		
		return dc;
	}
	
	public DoubleColumn divide(double divisor) {
		return apply(d -> d / divisor);
	}
	
	public DoubleColumn divideStream(double divisor) {
		return new DoubleColumn(values.stream().map(d -> d / divisor).collect(Collectors.toList()));
	}
	
	public DoubleColumn divideStreamFast(double divisor) {
		return new DoubleColumn(values.stream().mapToDouble(Double::doubleValue).map(d -> d / divisor).boxed().collect(Collectors.toList()));
	}
	
	public static void main(String[] args) {
		long t1 = 0;
		long t2 = 0;
		long t3 = 0;
		
		for (int i = 0; i < 10000; i++) {
			DoubleColumn dc = new DoubleColumn().rand(10000);
			
			long start = System.currentTimeMillis();
			dc.divide(5.0);
			long tt1 = System.currentTimeMillis() - start;
			
			start = System.currentTimeMillis();
			dc.divideStream(5.0);
			long tt2 = System.currentTimeMillis() - start;
			
			start = System.currentTimeMillis();
			dc.divideStreamFast(5.0);
			long tt3 = System.currentTimeMillis() - start;
			
			if (i > 10) {
				t1 += tt1;
				t2 += tt2;
				t3 += tt3;
			}
		}
		
		System.out.println(t1);
		System.out.println(t2);
		System.out.println(t3);
	}
	
}
