import j3.*
import j3.io.*
import java.io.*
import j3.widget.impl.*
import j3.widget.impl.scatter.*
import javafx.scene.shape.*

// clear the canvas
canvas.removeAll()

// load the IRIS dataset
def file = new File("data/iris.csv")
def reader = CanvasReaderFactory.getInstance().getReader(file)
reader.load(file, canvas)

// configure the 3D plot
def scatter3d = Selector.on(canvas).getFirst(Subscene3D)

scatter3d.scale.setX(1.2)
scatter3d.scale.setY(1.2)
scatter3d.scale.setZ(1.2)

// get the points on the canvas
def points = Selector.on(canvas).get(Box.class)

// circle one of the points
def tag = new Tag()
tag.target(points.get(0), canvas)
canvas.add(tag)

// add text annotation
def text = new TextAnnotation()
text.setText("This point is selected\nby this script.")
text.target(points.get(points.size()-1), canvas)
text.setLayoutX(50);
text.setLayoutY(50);
canvas.add(text)

// begin rotation
def axis3d = Selector.on(canvas).getFirst(Axis3D)
Transitions.on(axis3d).rotateY(1280, 60000).play()