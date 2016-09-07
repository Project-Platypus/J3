package j3.dataframe;

public interface Column<T> {
	
	public T get(int index);
	
	public void set(int index, Object value);
	
	//public Column<? extends T> apply(Function<? super T, ? extends T> op);

}
