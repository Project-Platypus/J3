# J3 #

A free desktop application for producing and sharing high-dimensional, interactive scientific visualizations.

![Screenshot](http://i.imgur.com/W2zqCTT.jpg)

## What is J3 ##

J3 is an open source, cross-platform application for producing and sharing high-dimensional, interactive scientific
visualizations.  While there are high-quality JavaScript libraries for producing visualization on the web, they are
often plagued by performance issues when dealing with large data sets.  J3, on the other hand, can fluidly support
thousands of data points by leveraging hardware accelerated graphics while simultaneously supporting animations and
interactivity.

## Get It ##

Pre-packaged distributions are available for Windows.  Distributions for Linux and Mac will be made available shortly.
All distributions include sample data files you can test.

### Windows ###

Download and extract [J3-Full.zip](https://github.com/MOEAFramework/J3/releases/download/1.0.0/J3-Win.zip) if you
already have Java 8 installed, or [J3-Full-JRE.zip](https://github.com/MOEAFramework/J3/releases/download/1.0.0/J3-Win-JRE.zip)
if you do not have Java 8.  After extracting, run `J3.exe`.  You can also load a data set from the command line by
running `J3.exe <file>`.

## FAQ ##

1. **What does J3 stand for?**  The name J3 is derived from the use of Java technologies and its design being influenced
   by the popular D3.js JavaScript library for "data driven documents."  J3 shares many similarities with D3, such as
   the ability to use selectors and transitions to manipulate data on the canvas, a DOM-like scene graph, and
   stylesheets for controlling the appearance of widgets.
   
2. **Why create J3 instead of using JavaScript libraries?**  J3 started as a way to learn JavaFX, but gradually evolved
   into the application you see today.  It was immediately apparent that a native application could provide similar
   flexibility with substantially improved performance.  J3 can easily render data sets with thousands of points,
   whereas JavaScript libraries must resort to clever tricks to remain interactive (e.g., sequential rendering).
   
3. **How can I contribute to J3?**  J3 is designed to be extensible.  Everything from themes, color maps, widgets, and
   supported file types is extensible.  Clone this repository and give it a shot.  If you have questions, please create
   an issue on Github.

## Design ##

At the core of J3 is the canvas, which contains the Property Registry and the
widgets.  The property registry is a collection of properties referenced by
name, similar to a map or dictionary.  These JavaFX properties offer several
advantages:

1. Store a shared object that can be referenced by name,
2. Provide change listeners so widgets can detect and react to changes in the underlying data model, and
3. Support binding, where changes automatically propogate to any bound properties,

For example, J3's property registry stores the plot axes as separate properties, 
such as "xAxis", "yAxis", or "colorAxis".  Any widget can bind to these properties,
and if any are changed, the widget can automatically invoke a change listener to
update the graphics.

To facilitate cooperation among widgets, a common set of properties is provided by
default.  Perhaps to most common data object provided is the "data frame".  This
data frame is used to load, store, and manipulate multivariate data.

Widgets are specialized objects that can be placed on the canvas, and typically have
some graphical components, such as displaying a plot.  J3 defines a lifecycle for
widgets, which allows a widget to become activated, initialized, added, or removed
from the canvas.  Furthermore, most widgets provide serialization, which allows J3
to store and reconstruct the layout.

J3 also attempts to combine Java 8's functional programming (lambdas) with
D3/jQuery-like selectors for manipulating the scene graph.  Combined with
JavaFX's reactive programming style (via properties and bindings) makes a
powerful means to update the scene graph in response to user inputs or changing
data.

## Share Data Objects ##

J3's property registry currently stores the following properties.  Note that these
properties may not exist, but some widgets will create and use them.

* data - The "data frame" storing the loaded multivariate data
* xAxis - The selected x-axis
* yAxis - The selected y-axis
* zAxis - The selected z-axis
* colorAxis - The selected color axis
* sizeAxis - The selected size axis
* visibilityAxis - Axis used by brushing to control the visibility of data points
* axes - A list of all available axes
* colormap - The name of the selected colormap
* selectedInstance - The selected data point (each point is called an "instance" in J3)
* theme - The name of the selected theme (e.g., light or dark)

J3 also mandates some standards to aid in cooperation.  Widgets wishing to interface
with existing functionality should follow these standards.

* Each instance in the data frame is assigned a unique id (a UUID).  This id should be
  used to reference instances, and should remain unchanged during serialization and
  deserialization.

* JavaFX nodes representing data points, either as points or other shapes, should
  set the node's id to a unique UUID and set the node's user data to the instance.
  This is used by J3's annotations to target specific nodes/instances and track that
  node/instance during the widget's lifecycle.

* A widget displaying data points should ensure ids remain consistent during serialization
  and deserialization.  Typically, this is handled by storing a mapping from the instance id
  to the node id.  This ensures that annotations targeting specific nodes/instances reference
  the correct id during deserialization.

* Widgets are generally activated using the widget menu.  Two standard mechanisms for adding
  widgets are provided: single-point clicking and box selection.  When a widget is activated
  (e.g., selected from the widget menu), it can register a callback with the canvas to be
  notified when the user has positioned the widget on the canvas.

* Input readers, including the DataFrameReader and CanvasReader, recognize files by their file
  extension.  A special "All Files (*.*)" reader is provided for convenience that lets the user
  pick any file.  The file extension is still used to determine the appropriate reader.  If no
  suitable reader is found or multiple readers match the same extension, the user is prompted
  to select the appropriate reader.
  
* Widgets that supports targeting (e.g., a scatter plot that supports tagging or annotations)
  should implements the `TargetableWidget` interface.  The canvas will automatically
  remove dependencies when the widget is removed.  The tags or annotation should register
  themselves as dependencies of the widget.

## Extensibility ##

J3 uses Java's service provider interface to support extensibility.  Currently, four extension
points are provided.

1. WidgetProvider - SPI for adding new widgets.  Widgets must follow the defined lifecycle for
   activating, initializing, adding, and removing widgets.

2. DataFrameReader - SPI for defining new input file formats.  The DataFrameReader only supports
   loading data frames (e.g., CSV, Excel, or other tabular data files).

3. CanvasReader - SPI for advanced input file formats.  The CanvasReader allows full customization
   of the loading process and can access the canvas directly when loading the file.

4. ColormapProvider - SPI for defining new colormaps.

Extensions should be packated in a JAR file with the appropriate `META-INF/services` file(s).
If distributing J3 as an executable, J3 will scan for the `plugins/` folder for any JARs.