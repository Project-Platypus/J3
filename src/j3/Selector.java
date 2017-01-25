package j3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;

/**
 * Mimics CSS or D3/jQuery selectors to quickly locate nodes within a JavaFX scene graph.  Supported types are:
 * <ul>
 *   <li>{@code *} - Matches all nodes</li>
 *   <li>{@code #id} - Matches the node's id</li>
 *   <li>{@code .style} - Matches any of the node's style classes</li>
 *   <li>{@code class} - Matches the Java class's simple name</li>
 * </ul>
 * The basic usage is {@code Selector.on(scene).select("#id").get()}.  A Java class can also be provided to the
 * {@code get} method for type-safety.  When matching against a unique id, it is more efficient to use
 * {@code getFirst()}.
 */
public class Selector {
	
	private Node onNode;
	
	private List<String> keys;
	
	private Selector() {
		super();
		
		keys = new ArrayList<>();
	}
	
	public static Selector on(Scene scene) {
		Selector selector = new Selector();
		selector.onNode = scene.getRoot();
		return selector;
	}
	
	public static Selector on(Node node) {
		Selector selector = new Selector();
		selector.onNode = node;
		return selector;
	}
	
	public Selector select(String key) {
		keys.addAll(Arrays.stream(key.split("\\s+")).map(String::trim).collect(Collectors.toList()));
		return this;
	}
	
	public List<Node> get() {
		return get(onNode, keys, Node.class);
	}
	
	public <T> List<T> get(Class<T> type) {
		return get(onNode, keys, type);
	}
	
	private <T> List<T> get(Node node, List<String> keys, Class<T> type) {
		List<T> result = new ArrayList<>();
		
		if (type.isInstance(node) && matches(node, keys)) {
			result.add(type.cast(node));
		}
		
		if (node instanceof Parent) {
			for (Node child : ((Parent)node).getChildrenUnmodifiable()) {
				result.addAll(get(child, keys, type));
			}
		} else if (node instanceof SubScene) {
			result.addAll(get(((SubScene)node).getRoot(), keys, type));
		}
		
		return result;
	}
	
	public Node getFirst() {
		return getFirst(Node.class);
	}
	
	public <T> T getFirst(Class<T> type) {
		return getFirst(onNode, keys, type);
	}
	
	private <T> T getFirst(Node node, List<String> keys, Class<T> type) {
		if (type.isInstance(node) && matches(node, keys)) {
			return type.cast(node);
		}
		
		if (node instanceof Parent) {
			for (Node child : ((Parent)node).getChildrenUnmodifiable()) {
				T result = getFirst(child, keys, type);
				
				if (result != null) {
					return result;
				}
			}
		} else if (node instanceof SubScene) {
			T result = getFirst(((SubScene)node).getRoot(), keys, type);
			
			if (result != null) {
				return result;
			}
		}
		
		return null;
	}
	
	private boolean matches(Node node, List<String> keys) {
		if (keys.isEmpty()) {
			return true;
		}
		
		return keys.stream().anyMatch(key -> {
			if (key.equals("*")) {
				return true;
			} else if (key.startsWith(".")) {
				return node.getStyleClass().contains(key.substring(1));
			} else if (key.startsWith("#")) {
				return node.getId() != null && node.getId().equals(key.substring(1));
			} else {
				return key.getClass().getSimpleName().equals(key);
			}
		});
	}

}
