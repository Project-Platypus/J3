package j3.dataframe;

public abstract class Attribute<T> {

	protected String name;

	protected final Class<? extends T> type;

	public Attribute(String name, Class<? extends T> type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	public Class<? extends T> getType() {
		return type;
	}

	/**
	 * Converts an object to the type required by this attribute. Generally, an
	 * attribute can only store values of a single type, but are free to convert
	 * objects of different types. For example, a numeric attribute can choose to
	 * automatically widen inputs (e.g., Byte -> Short -> Integer -> Long -> Float
	 * -> Double).
	 * 
	 * @param object the object to convert
	 * @return the converted value
	 * @throws ClassConversionException if the object could not be converted to the
	 *                                  type required by this attribute
	 */
	public abstract T convert(Object object);

}
