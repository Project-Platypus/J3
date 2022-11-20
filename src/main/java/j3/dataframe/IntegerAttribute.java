package j3.dataframe;

public class IntegerAttribute extends Attribute<Integer> {

	public IntegerAttribute(String name) {
		super(name, Integer.class);
	}

	@Override
	public Integer convert(Object object) throws ClassConversionException {
		if (object instanceof Integer) {
			return (Integer) object;
		} else if (object instanceof Byte || object instanceof Short) {
			return ((Number) object).intValue();
		} else if (object instanceof Character) {
			return Character.getNumericValue((Character) object);
		} else if (object instanceof String) {
			try {
				return Integer.parseInt((String) object);
			} catch (NumberFormatException e) {
				throw new ClassConversionException(object, type, e);
			}
		} else {
			throw new ClassConversionException(object, type);
		}
	}

}
