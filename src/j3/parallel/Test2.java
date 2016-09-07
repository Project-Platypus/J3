package j3.parallel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Test2 extends Application {
	
	int ncol = 5;
	
	private NumberAxis[] axes = new NumberAxis[ncol];
	
	private List<Integer> permutation = new ArrayList<Integer>();
	
	private List<double[]> values = new ArrayList<double[]>();
	
	private List<Color> colors = new ArrayList<Color>();
	
	private Point2D initPoint;
	
	private Point2D anchorPoint;
	
	private final Group group = new Group();
	
	private final Group lineGroup = new Group();
	
	private List<Point2D> brushLimits = new ArrayList<Point2D>();
	
	private List<Rectangle> brushShape = new ArrayList<Rectangle>();
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(group, 800, 600, false, SceneAntialiasing.DISABLED);
		
		Group axisGroup = new Group();
		
		for (int i = 0; i < ncol; i++) {
			final int index = i;
			
			brushLimits.add(null);
			brushShape.add(new Rectangle());
			
			NumberAxis axis = new NumberAxis("axis" + index, 0, 1, 0.5);
			axis.setSide(Side.LEFT);
			
			double spaceWidth = (scene.getWidth()-60.0)/ncol;
			double spaceCenter = 30.0 + index*spaceWidth + spaceWidth/2.0;
			axis.setLayoutY(30);
			axis.setPrefHeight(scene.getHeight()-60);
			axis.setLayoutX(spaceCenter - axis.getWidth()/2.0);
			//axis.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
			axis.setStyle("-fx-border-color: transparent transparent transparent transparent;"
					+ " -fx-tick-label-font-size: 1.0em;"
					+ " -fx-tick-label-fill: black;"
					+ " -fx-tick-label-font-weight: bold;"
					+ " -fx-font-weight: bold;");
			
			axes[i] = axis;
			permutation.add(i);
			
			scene.heightProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> property,
						Number oldValue, Number newValue) {
					axis.setLayoutY(30);
					axis.setPrefHeight(newValue.doubleValue()-60);
				}
				
			});
			
			scene.widthProperty().addListener(new ChangeListener<Number>() {

				@Override
				public void changed(ObservableValue<? extends Number> property,
						Number oldValue, Number newValue) {
					double spaceWidth = (newValue.doubleValue()-60.0)/ncol;
					double spaceCenter = 30.0 + index*spaceWidth + spaceWidth/2.0;
					
					axis.setLayoutX(spaceCenter - axis.getWidth()/2.0);
				}
				
			});
			
			axis.axisLabel.setCursor(Cursor.H_RESIZE);
			
			axis.axisLabel.setOnMousePressed(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent me) {
					initPoint = new Point2D(axis.getTranslateX(), axis.getTranslateY());
					anchorPoint = new Point2D(me.getSceneX(), me.getSceneY());
				}
				
			});
			
			axis.axisLabel.setOnMouseReleased(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent me) {	
					for (int i = 0; i < ncol; i++) {
						permutation.set(i, i);
					}
					
					Collections.sort(permutation, new Comparator<Integer>() {

						@Override
						public int compare(Integer o1, Integer o2) {
							return Double.compare(axes[o1].getLayoutX()+axes[o1].getTranslateX(), axes[o2].getLayoutX()+axes[o2].getTranslateX());
						}
						
					});
					
					double[] oldPositions = new double[ncol];
					double[] newPositions = new double[ncol];
					
					for (int i = 0; i < ncol; i++) {
						double spaceWidth = (scene.getWidth()-60.0)/ncol;
						double spaceCenter = 30.0 + i*spaceWidth + spaceWidth/2.0;
						double newLayout = spaceCenter - axis.getWidth()/2.0;

						oldPositions[i] = axes[permutation.get(i)].getLayoutX();
						newPositions[i] = newLayout;
					}
					
					relayoutLines();
					
					Timeline timeline = new Timeline();
					
					for (int i = 0; i < ncol; i++) {
							timeline.getKeyFrames().addAll(
									new KeyFrame(Duration.ZERO,
											new KeyValue(axes[permutation.get(i)].layoutXProperty(), oldPositions[i])),
									new KeyFrame(new Duration(250),
											new KeyValue(axes[permutation.get(i)].layoutXProperty(), newPositions[i])));							
					}
					
					timeline.getKeyFrames().addAll(
							new KeyFrame(Duration.ZERO,
									new KeyValue(axes[index].translateXProperty(), axes[index].getTranslateX())),
							new KeyFrame(new Duration(250),
									new KeyValue(axes[index].translateXProperty(), 0.0)));
					
					timeline.play();
					
					initPoint = null;
					anchorPoint = null;
				}
				
			});
			
			axis.axisLabel.setOnMouseDragged(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent me) {
					axis.setTranslateX(initPoint.getX()+(me.getSceneX()-anchorPoint.getX()));
					
					List<Integer> newPermutation = new ArrayList<Integer>();
					
					for (int i = 0; i < ncol; i++) {
						newPermutation.add(i);
					}
					
					Collections.sort(newPermutation, new Comparator<Integer>() {

						@Override
						public int compare(Integer o1, Integer o2) {
							return Double.compare(axes[o1].getLayoutX()+axes[o1].getTranslateX(), axes[o2].getLayoutX()+axes[o2].getTranslateX());
						}
						
					});

					if (!newPermutation.equals(permutation)) {
						permutation = newPermutation;
						
						double[] oldPositions = new double[ncol];
						double[] newPositions = new double[ncol];
						
						for (int i = 0; i < ncol; i++) {
							double spaceWidth = (scene.getWidth()-60.0)/ncol;
							double spaceCenter = 30.0 + i*spaceWidth + spaceWidth/2.0;
							double newLayout = spaceCenter - axis.getWidth()/2.0;
							
							oldPositions[i] = axes[permutation.get(i)].getLayoutX();
							newPositions[i] = newLayout;
						}
						
						relayoutLines();
						
						Timeline timeline = new Timeline();
						
						for (int i = 0; i < ncol; i++) {
							if (index != permutation.get(i)) {
								timeline.getKeyFrames().addAll(
										new KeyFrame(Duration.ZERO,
												new KeyValue(axes[permutation.get(i)].layoutXProperty(), oldPositions[i])),
										new KeyFrame(new Duration(250),
												new KeyValue(axes[permutation.get(i)].layoutXProperty(), newPositions[i])));							
							}
						}
						
						timeline.play();
					}
				}
				
			});

			axis.axisBody.setCursor(Cursor.V_RESIZE);
			
			axis.axisBody.setOnMousePressed(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent me) {
					initPoint = new Point2D(axis.axisBody.getTranslateX(), axis.axisBody.getTranslateY());
					anchorPoint = new Point2D(me.getSceneX(), me.getSceneY());
				}
				
			});
			
			axis.axisBody.setOnMouseDragged(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent me) {
					Rectangle r = brushShape.get(index);
					
					if (me.getSceneY() < anchorPoint.getY()) {
						brushLimits.set(index, new Point2D((me.getSceneY()-30)/axis.getHeight(), (anchorPoint.getY()-30)/axis.getHeight()));
						
						r.setLayoutX(initPoint.getX());
						r.setLayoutY(me.getSceneY()-30);
						r.setWidth(axis.getWidth());
						r.setHeight(anchorPoint.getY()-me.getSceneY());
					} else {
						brushLimits.set(index, new Point2D((anchorPoint.getY()-30)/axis.getHeight(), (me.getSceneY()-30)/axis.getHeight()));
						
						r.setLayoutX(initPoint.getX());
						r.setLayoutY(anchorPoint.getY()-30);
						r.setWidth(axis.getWidth());
						r.setHeight(me.getSceneY()-anchorPoint.getY());
					}
					
					r.setFill(Color.web("#00000020"));
					
					if (!axis.axisBody.getChildren().contains(r)) {
						axis.axisBody.getChildren().add(r);
					}
					
					relayoutLines();
				}
				
			});
			
			axisGroup.getChildren().add(axis);
		}
		
		Random random = new Random();
		
		for (int i = 0; i < 1000; i++) {
			double[] value = new double[ncol];
			
			for (int j = 0; j < ncol; j++) {
				value[j] = random.nextDouble();
			}
			
			values.add(value);
			colors.add(Color.hsb(value[0]*360, 1.0, 1.0));
		}
		
		relayoutLines();
		group.getChildren().add(lineGroup);
		group.getChildren().add(axisGroup);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private List<Line> allLines = new ArrayList<Line>();
	
	public void relayoutLines() {
		lineGroup.getChildren().clear();
		boolean create = allLines.isEmpty();
		
		for (int i = 0; i < values.size(); i++) {
			boolean isBrushed = true;
			
			for (Point2D point : brushLimits) {
				if (point != null) {
					isBrushed = false;
					break;
				}
			}
			
			if (!isBrushed) {
				isBrushed = true;
				
						for (int j = 0; j < ncol; j++) {
							Point2D point = brushLimits.get(j);
							
							if (point != null) {
								if (!(values.get(i)[j] >= point.getX() && values.get(i)[j] <= point.getY())) {
									isBrushed = false;
									break;
								}
							}
				}
			}
			
			for (int j = 0; j < ncol-1; j++) {
				NumberAxis axis1 = axes[permutation.get(j)];
				NumberAxis axis2 = axes[permutation.get(j+1)];
				Line line = null;
				
				if (create) {
					line = new Line();
					allLines.add(line);
				} else {
					line = allLines.get(i*(ncol-1)+j);
				}
				
				line.setStrokeWidth(1.5);
				line.startXProperty().bind(axis1.layoutXProperty().add(axis1.translateXProperty()).add(axis1.widthProperty().divide(2.0)));
				line.startYProperty().bind(axis1.layoutYProperty().add(axis1.heightProperty().multiply(values.get(i)[permutation.get(j)])));
				line.endXProperty().bind(axis2.layoutXProperty().add(axis2.translateXProperty()).add(axis2.widthProperty().divide(2.0)));
				line.endYProperty().bind(axis2.layoutYProperty().add(axis2.heightProperty().multiply(values.get(i)[permutation.get(j+1)])));
				
				if (isBrushed) {
					line.setStroke(colors.get(i));
				} else {
					line.setStroke(Color.color(colors.get(i).getRed(), colors.get(i).getGreen(), colors.get(i).getBlue(), 0.05));
				}
				
				line.setManaged(false);
				lineGroup.getChildren().add(line);
			}
		}
	}

}
