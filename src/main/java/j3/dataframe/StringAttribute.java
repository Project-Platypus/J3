package j3.dataframe;

public class StringAttribute extends Attribute<String> {

	public StringAttribute(String name) {
		super(name, String.class);
	}

	@Override
	public String convert(Object object) throws ClassConversionException {
		return object.toString();
	}

}
