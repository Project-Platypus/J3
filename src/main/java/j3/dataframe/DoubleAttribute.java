package j3.dataframe;

public class DoubleAttribute extends Attribute<Double> {

	public DoubleAttribute(String name) {
		super(name, Double.class);
	}

	@Override
	public Double convert(Object object) throws ClassConversionException {
		if (object instanceof Double) {
			return (Double) object;
		} else if (object instanceof Byte || object instanceof Short || object instanceof Integer
				|| object instanceof Long || object instanceof Float) {
			return ((Number) object).doubleValue();
		} else if (object instanceof Character) {
			return (double) Character.getNumericValue((Character) object);
		} else if (object instanceof String) {
			try {
				return Double.parseDouble((String) object);
			} catch (NumberFormatException e) {
				throw new ClassConversionException(object, type, e);
			}
		} else {
			throw new ClassConversionException(object, type);
		}
	}

}
