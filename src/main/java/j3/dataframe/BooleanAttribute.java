package j3.dataframe;

public class BooleanAttribute extends Attribute<Boolean> {

	public BooleanAttribute(String name) {
		super(name, Boolean.class);
	}

	@Override
	public Boolean convert(Object object) {
		if (object instanceof Boolean) {
			return (Boolean) object;
		} else if (object instanceof String) {
			return Boolean.parseBoolean((String) object);
		} else {
			throw new ClassConversionException(object, type);
		}
	}

}
