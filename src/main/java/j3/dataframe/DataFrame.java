package j3.dataframe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A data frame is a collection of instances and their attributes.  This is
 * similar in nature of R's data.frame or a matrix, except types are enforced.
 * {@code Instance}s are analogous to rows while {@code Attribute}s are columns.
 * 
 * A data frame differs from other containers in that it offers weak (dynamic)
 * typing.  Each attribute is associated with a type that is checked at runtime.
 * A {@link ClassConversionException} is thrown when attempting to insert an
 * invalid type.  Attributes are able to convert compatible types, such as
 * widening numeric values or parsing strings.  Refer to the documentation for
 * each attribute to learn more.
 * 
 * Also, unlike other data frame implementations, here attributes, instances,
 * and the data frame are loosely coupled.  An instance binds one or more
 * attributes to their values.  A data frame is a collection of instances and
 * their defined attributes.  There is, however, no requirement that an
 * instance reside in a data frame.  Adding a new attribute to an instance does
 * not automatically add the attribute to the data frame; instead it must be
 * added to both.
 */
public class DataFrame {
	
	private final List<Attribute<?>> attributes;
	
	private final List<Instance> instances;

	public DataFrame(Attribute<?>... attributes) {
		this(Arrays.asList(attributes));
	}
	
	public DataFrame(Iterable<Attribute<?>> attributes) {
		super();
		
		this.attributes = new ArrayList<>();
		this.instances = new ArrayList<>();
		
		for (Attribute<?> attribute : attributes) {
			addAttribute(attribute);
		}
	}
	
	public void addAttribute(Attribute<?> attribute) {
		attributes.add(attribute);
	}
	
	public void removeAttribute(Attribute<?> attribute) {
		attributes.remove(attribute);
	}
	
	public void removeAttribute(int index) {
		attributes.remove(index);
	}
	
	public Attribute<?> getAttribute(int index) {
		return attributes.get(index);
	}
	
	public void addInstance(Instance instance) {
		instances.add(instance);
	}
	
	public void removeInstance(Instance instance) {
		instances.remove(instance);
	}
	
	public void removeInstance(int index) {
		instances.remove(index);
	}
	
	public Instance getInstance(int index) {
		return instances.get(index);
	}
	
	public Instance getInstance(UUID id) {
		for (Instance instance : instances) {
			if (instance.getId().equals(id)) {
				return instance;
			}
		}
		
		return null;
	}
	
	public List<Attribute<?>> getAttributes() {
		return new ArrayList<Attribute<?>>(attributes);
	}
	
	public List<Instance> getInstances() {
		return new ArrayList<Instance>(instances);
	}
	
	public int instanceCount() {
		return instances.size();
	}
	
	public int attributeCount() {
		return attributes.size();
	}
	
	public Iterator<Attribute<?>> attributeIterator() {
		return attributes.iterator();
	}
	
	public Iterator<Instance> instanceIterator() {
		return instances.iterator();
	}
	
	public List<?> getValues(int index) {
		Attribute<?> attribute = getAttribute(index);
		return instances.stream().map(instance -> instance.get(attribute)).collect(Collectors.toList());
	}
	
	public <T> List<T> getValues(Attribute<? extends T> attribute) {
		return instances.stream().map(instance -> instance.get(attribute)).collect(Collectors.toList());
	}
	
	public boolean hasMissingValues() {
		return attributes.stream().anyMatch(attribute -> {
			return instances.stream().anyMatch(instance -> {
				return instance.get(attribute) == null;
			});
		});
	}

}
