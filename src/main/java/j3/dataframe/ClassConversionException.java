package j3.dataframe;

public class ClassConversionException extends RuntimeException {

	private static final long serialVersionUID = 6292288565842566560L;
	
	public ClassConversionException(Object object, Class<?> type) {
		super(String.format("Unable to convert %s to %s", object, type));
	}
	
	public ClassConversionException(Object object, Class<?> type, Throwable cause) {
		super(String.format("Unable to convert %s to %s", object, type), cause);
	}
	
}
