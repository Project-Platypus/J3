package j3.dataframe;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class MagicTyping {

	private List<Pair<Predicate<? super Object>, Function<String, Attribute<?>>>> conversions;

	public MagicTyping() {
		super();

		conversions = new ArrayList<>();
		conversions.add(new ImmutablePair<>(isInteger, (name) -> new IntegerAttribute(name)));
		conversions.add(new ImmutablePair<>(isDouble, (name) -> new DoubleAttribute(name)));
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
		for (Pair<Predicate<? super Object>, Function<String, Attribute<?>>> conversion : conversions) {
			if (frame.getInstances().stream().map(i -> (Object) i.get(attribute)).allMatch(conversion.getKey())) {
				return conversion.getValue().apply(attribute.getName());
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
