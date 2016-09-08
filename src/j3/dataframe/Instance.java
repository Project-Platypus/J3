package j3.dataframe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Instance {
	
	private final Map<Attribute<?>, Object> values;
	
	public Instance() {
		super();
		values = new HashMap<>();
	}
	
	public Set<Attribute<?>> getAttributes() {
		return new HashSet<>(values.keySet());
	}
	
	public <T> T get(Attribute<? extends T> attribute) {
		Object value = values.get(attribute);
		
		if (value == null) {
			return null;
		} else {
			return attribute.getType().cast(values.get(attribute));
		}
	}
	
	public <T> void set(Attribute<?> attribute, Object value) {
		values.put(attribute, attribute.convert(value));
	}
	
	public void remove(Attribute<?> attribute) {
		values.remove(attribute);
	}

}
