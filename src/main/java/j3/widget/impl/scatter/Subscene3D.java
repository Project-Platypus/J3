package j3.widget.impl.scatter;

import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class Subscene3D extends SubScene {

	public static enum MouseMode {

		NONE,

		ROTATE,

		TRANSLATE,

		SCALE

	}

	public static enum ScrollMode {

		NONE,

		SCALE

	}

	private Axis3D axis3D;

	private MouseMode mouseMode;

	private ScrollMode scrollMode;

	private Group root;

	private Group textGroup;

	private double mousePosX, mousePosY;
	private double mouseOldX, mouseOldY;
	private double scaleOld;
	public final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
	public final Rotate rotateY = new Rotate(-45, Rotate.Y_AXIS);
	protected final Translate translate = new Translate(0, 0, 0);
	protected final Scale scale = new Scale(0.5, 0.5, 0.5);

	public Subscene3D(int size) {
		super(new Group(), size, size, true, SceneAntialiasing.BALANCED);

		// initialize the camera
		setCamera(new PerspectiveCamera());

		// initialize the nodes within this scene
		root = (Group) getRoot();
		textGroup = new Group();
		axis3D = new Axis3D(size, textGroup);

		// setup the transforms for the 3D axis, centering it on screen
		translate.setX(getWidth() / 2.0);
		translate.setY(getHeight() / 2.0);
		axis3D.getTransforms().addAll(translate, rotateX, rotateY, scale);

		root.getChildren().addAll(axis3D, textGroup);

		// initialize event handlers
		setMouseMode(MouseMode.ROTATE);
		setScrollMode(ScrollMode.SCALE);

		// bind width and height to the
		widthProperty().addListener((observable, oldValue, newValue) -> {
			double fracX = (translate.getX() + axis3D.getWidth() / 2.0) / oldValue.doubleValue();
			double newX = (fracX * newValue.doubleValue()) - axis3D.getWidth() / 2.0;

			translate.setX(newX);
		});

		heightProperty().addListener((observable, oldValue, newValue) -> {
			double fracY = (translate.getY() + axis3D.getHeight() / 2.0) / oldValue.doubleValue();
			double newY = (fracY * newValue.doubleValue()) - axis3D.getHeight() / 2.0;

			translate.setY(newY);
		});

		setManaged(false);
	}

	public Axis3D getAxis3D() {
		return axis3D;
	}

	protected MouseMode getMouseMode() {
		return mouseMode;
	}

	public void setMouseMode(MouseMode mouseMode) {
		if (this.mouseMode != mouseMode) {
			this.mouseMode = mouseMode;

			switch (mouseMode) {
			case NONE:
				setOnMousePressed(null);
				setOnMouseDragged(null);
				setOnMouseReleased(null);
				break;
			case ROTATE:
				setOnMousePressed(me -> {
					if (me.isConsumed()) {
						return;
					}

					mouseOldX = me.getSceneX();
					mouseOldY = me.getSceneY();
				});

				setOnMouseDragged(me -> {
					if (me.isConsumed()) {
						return;
					}

					mousePosX = me.getSceneX();
					mousePosY = me.getSceneY();
					rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY));
					rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX));
					mouseOldX = mousePosX;
					mouseOldY = mousePosY;
				});

				break;
			case TRANSLATE:
				setOnMousePressed(me -> {
					if (me.isConsumed()) {
						return;
					}

					mouseOldX = me.getSceneX();
					mouseOldY = me.getSceneY();
				});

				setOnMouseDragged(me -> {
					if (me.isConsumed()) {
						return;
					}

					mousePosX = me.getSceneX();
					mousePosY = me.getSceneY();
					translate.setX(translate.getX() + (mousePosX - mouseOldX));
					translate.setY(translate.getY() + (mousePosY - mouseOldY));
					mouseOldX = mousePosX;
					mouseOldY = mousePosY;
				});

				break;
			case SCALE:
				setOnMousePressed(me -> {
					if (me.isConsumed()) {
						return;
					}

					mouseOldY = me.getSceneY();
					scaleOld = scale.getX();
				});

				setOnMouseDragged(me -> {
					if (me.isConsumed()) {
						return;
					}

					mousePosY = me.getSceneY();

					double diff = mouseOldY - mousePosY;
					double frac = 1.0 + diff / getHeight();
					double scaleValue = scaleOld * frac;

					scale.setX(scaleValue);
					scale.setY(scaleValue);
					scale.setZ(scaleValue);
				});

				break;
			}
		}
	}

	protected ScrollMode getScrollMode() {
		return scrollMode;
	}

	protected void setScrollMode(ScrollMode scrollMode) {
		if (this.scrollMode != scrollMode) {
			this.scrollMode = scrollMode;

			switch (scrollMode) {
			case NONE:
				setOnScroll(null);
				break;
			case SCALE:
				setOnScroll(event -> {
					double delta = 1.2;
					double scaleValue = scale.getX();

					if (event.getDeltaY() < 0) {
						scaleValue /= delta;
					} else {
						scaleValue *= delta;
					}

					if (scaleValue < 0.001) {
						scaleValue = 0.001;
					} else if (scaleValue > 45) {
						scaleValue = 45;
					}

					scale.setX(scaleValue);
					scale.setY(scaleValue);
					scale.setZ(scaleValue);

					event.consume();
				});
				break;
			}
		}
	}

}
