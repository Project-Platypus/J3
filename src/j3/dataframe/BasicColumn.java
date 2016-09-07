package j3.dataframe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class BasicColumn<T> implements Column<T> {

	private Class<T> type;
	
	protected List<T> values;
	
	public BasicColumn(Class<T> type) {
		super();
		this.type = type;
		
		values = new ArrayList<>();
		values.add(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		return (T)values.get(index);
	}

	@Override
	public void set(int index, Object value) {
		values.set(index, type.cast(value));
	}

//	@Override
//	public Column<? extends T> apply(Function<? super T, ? extends T> op) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
}
