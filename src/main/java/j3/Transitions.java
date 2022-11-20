package j3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

public class Transitions {

	private List<Node> onNodes;

	private double delay;

	private Transition currentTransition;

	private ParallelTransition parallelTransition;

	private Transitions() {
		super();

		parallelTransition = new ParallelTransition();
	}

	public static Transitions on(Node... nodes) {
		Transitions transitions = new Transitions();
		transitions.onNodes = Arrays.asList(nodes);
		return transitions;
	}

	public static Transitions on(Collection<Node> nodes) {
		Transitions transitions = new Transitions();
		transitions.onNodes = new ArrayList<Node>(nodes);
		return transitions;
	}

	private Rotate locateOrCreateRotation(Node node, Point3D axis) {
		Rotate rotate = null;

		for (Transform transform : node.getTransforms()) {
			if (transform instanceof Rotate) {
				Rotate tempRotate = (Rotate) transform;

				if (tempRotate.getAxis().equals(axis)) {
					rotate = tempRotate;
					break;
				}
			}
		}

		if (rotate == null) {
			rotate = new Rotate();
			rotate.setAxis(axis);
			node.getTransforms().add(rotate);
		}

		return rotate;
	}

	public Transitions rotateX(double angle, int duration) {
		for (Node node : onNodes) {
			rotate(node, angle, duration, Rotate.X_AXIS);
		}

		return this;
	}

	public Transitions rotateY(double angle, int duration) {
		for (Node node : onNodes) {
			rotate(node, angle, duration, Rotate.Y_AXIS);
		}

		return this;
	}

	public Transitions rotateZ(double angle, int duration) {
		for (Node node : onNodes) {
			rotate(node, angle, duration, Rotate.Z_AXIS);
		}

		return this;
	}

	private void rotate(Node node, double angle, int duration, Point3D axis) {
		Rotate rotate = locateOrCreateRotation(node, axis);

		currentTransition = new Transition() {

			private double initialAngle;

			{
				setCycleCount(1);
				setCycleDuration(new Duration(duration));
				setDelay(new Duration(delay));
			}

			@Override
			protected void interpolate(double frac) {
				if (frac == 0.0) {
					initialAngle = rotate.getAngle();
				}

				rotate.setAngle(initialAngle + frac * angle);
			}

		};

		parallelTransition.getChildren().add(currentTransition);
	}

	private Scale locateOrCreateScale(Node node) {
		Scale scale = null;

		for (Transform transform : node.getTransforms()) {
			if (transform instanceof Scale) {
				scale = (Scale) transform;
				break;
			}
		}

		if (scale == null) {
			scale = new Scale();
			node.getTransforms().add(scale);
		}

		return scale;
	}

	public Transitions scale(double factor, int duration) {
		for (Node node : onNodes) {
			scale(node, factor, duration);
		}

		return this;
	}

	private void scale(Node node, double factor, int duration) {
		Scale scale = locateOrCreateScale(node);

		currentTransition = new Transition() {

			private double initialX;
			private double initialY;
			private double initialZ;

			{
				setCycleCount(1);
				setCycleDuration(new Duration(duration));
				setDelay(new Duration(delay));
			}

			@Override
			protected void interpolate(double frac) {
				if (frac == 0.0) {
					initialX = scale.getX();
					initialY = scale.getY();
					initialZ = scale.getZ();
				}

				scale.setX(initialX + frac * factor);
				scale.setY(initialY + frac * factor);
				scale.setZ(initialZ + frac * factor);
			}

		};

		parallelTransition.getChildren().add(currentTransition);
	}

	public Transitions waitFor() {
		delay = currentTransition.getDelay().toMillis()
				+ currentTransition.getCycleDuration().toMillis() * currentTransition.getCycleCount();
		return this;
	}

	public Transitions waitFor(double duration) {
		delay += duration;
		return this;
	}

	public void play() {
		parallelTransition.play();
	}

}
