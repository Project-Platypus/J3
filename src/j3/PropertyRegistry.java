package j3;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class PropertyRegistry {
	
	private ObservableMap<String, ObjectProperty<Object>> properties;
	
	public PropertyRegistry() {
		super();
		
		properties = FXCollections.observableHashMap();
	}
	
	public <T> ObjectProperty<T> put(String key, T value) {
		return put(key, value, key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ObjectProperty<T> put(String key, T value, String propertyName) {
		ObjectProperty<Object> property = null;
		
		if (value instanceof ObjectProperty) {
			property = (ObjectProperty<Object>)value;
		} else {
			property = new ObjectPropertyBase<Object>(value) {

				@Override
				public Object getBean() {
					return PropertyRegistry.this;
				}

				@Override
				public String getName() {
					return propertyName;
				}
				
			};
		}
		
		properties.put(key, property);
		
		return (ObjectProperty<T>)property;
	}
	
	public <T> ObjectProperty<T> get(String key) {
		return get(key, null);
	}
	
	public <T> ObjectProperty<T> get(String key, T defaultValue) {
		return get(key, defaultValue, key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> ObjectProperty<T> get(String key, T defaultValue, String propertyName) {
		ObjectProperty<Object> result = properties.get(key);
		
		if (result == null) {
			result = new ObjectPropertyBase<Object>(defaultValue) {

				@Override
				public Object getBean() {
					return PropertyRegistry.this;
				}

				@Override
				public String getName() {
					return propertyName;
				}
				
			};
			
			properties.put(key, result);
		}
		
		return (ObjectProperty<T>)result;
	}
	
	public boolean contains(String key) {
		return properties.containsKey(key);
	}

}
