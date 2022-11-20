package j3.dataframe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Instance {

	private final Map<Attribute<?>, Object> values;

	private UUID id;

	public Instance() {
		super();
		values = new HashMap<>();
		id = UUID.randomUUID();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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
