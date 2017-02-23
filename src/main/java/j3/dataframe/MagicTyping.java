package j3.dataframe;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class MagicTyping {
	
	private List<Pair<Predicate<? super Object>, Class<? extends Attribute<?>>>> conversions;
	
	public MagicTyping() {
		super();
		
		conversions = new ArrayList<>();
		conversions.add(new ImmutablePair<>(isInteger, IntegerAttribute.class));
		conversions.add(new ImmutablePair<>(isDouble, DoubleAttribute.class));
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
		for (Pair<Predicate<? super Object>, Class<? extends Attribute<?>>> conversion : conversions) {
			if (frame.getInstances().stream().map(i -> (Object)i.get(attribute)).allMatch(conversion.getKey())) {
				try {
					return conversion.getValue().getDeclaredConstructor(String.class).newInstance(attribute.getName());
				} catch (Exception e) {
					throw new RuntimeException("Unable to call constructor for " + conversion.getValue());
				}
			}
		}
		
		return attribute;
	}
	
	private static Predicate<? super Object> isDouble = value -> {
		if (value instanceof Number) {
			return true;
		} else if (value instanceof String) {
			try {
				Double.parseDouble(value.toString());
				return true;
			} catch (NumberFormatException ex) {
				return false;
			}
		} else {
			return false;
		}
	};
	
	private static Predicate<? super Object> isInteger = value -> {
		if ((value instanceof Integer) || (value instanceof Short) || (value instanceof Byte)) {
			return true;
		} else if (value instanceof String) {
			try {
				Integer.parseInt(value.toString());
				return true;
			} catch (NumberFormatException ex) {
				return false;
			}
		} else {
			return false;
		}
	};
	
}
