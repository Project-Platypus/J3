package j3;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class SharedData {
	
	private ObservableMap<String, ObjectProperty<?>> properties;
	
	public SharedData() {
		super();
		
		properties = FXCollections.observableHashMap();
	}
	
	public <T extends Object> ObjectProperty<T> put(String key, T value) {
		return put(key, value, key);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> ObjectProperty<T> put(String key, T value, String propertyName) {
		ObjectProperty<T> property = null;
		
		if (value instanceof ObjectProperty) {
			property = (ObjectProperty<T>)value;
		} else {
			property = new ObjectPropertyBase<T>(value) {

				@Override
				public Object getBean() {
					return SharedData.this;
				}

				@Override
				public String getName() {
					return propertyName;
				}
				
			};
		}
		
		properties.put(key, property);
		return property;
	}
	
	public ObjectProperty<? extends Object> get(String key) {
		return properties.get(key);
	}
	
	public boolean contains(String key) {
		return properties.containsKey(key);
	}

}
