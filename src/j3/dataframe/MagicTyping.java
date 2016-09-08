package j3.dataframe;

import java.util.function.Predicate;

public class MagicTyping {
	
	public enum Type {
		
		DOUBLE,
		
		STRING
		
	}

	public void convert(DataFrame frame) {
		for (Attribute<?> oldAttribute : frame.getAttributes()) {
			Attribute<?> newAttribute = determineType(frame, oldAttribute);
			
			if (newAttribute != oldAttribute) {
				frame.getInstances().forEach(instance -> {
					instance.set(newAttribute, instance.get(oldAttribute));
					instance.remove(oldAttribute);
				});
			}
			
			frame.removeAttribute(oldAttribute);
			frame.addAttribute(newAttribute);
		}
	}
	
	private Attribute<?> determineType(DataFrame frame, Attribute<?> attribute) {
		boolean isDouble = frame.getInstances().stream().map(i -> (Object)i.get(attribute)).allMatch(this.isDouble);
		
		if (isDouble) {
			return new DoubleAttribute(attribute.getName());
		} else {
			return attribute;
		}
	}
	
	private static Predicate<? super Object> isDouble = value -> {
		if (value instanceof Number) {
			return true;
		} else if (value instanceof String) {
			try {
				Double.parseDouble(value.toString());
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else {
			return false;
		}
	};
	
}
