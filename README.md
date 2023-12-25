# J3

**A free desktop application for producing and sharing high-dimensional, interactive scientific visualizations.**

![Screenshot](http://i.imgur.com/W2zqCTT.jpg)

## Key Features

1. Interactive 2D and 3D plots (scatter, parallel axis, etc.)
2. Various annotations and data callouts
3. Ability to save, share, and load visualizations
4. Create scripted animations (experimental)
5. Extensible design allows adding new widgets

## Usage

J3 is a Java application built using JavaFX.  We recommend installing a version of Java with JavaFX built-in,
such as [Zulu](https://www.azul.com/downloads).  Pick `JDK FX" under the Java Package filter.  Java 17 or newer
is required.

### Running from Source

If using Eclipse, run the class `j3.GUI` directly or create a new Maven run configuration with
the goal `javafx:run`.

If using Maven, run `mvn javafx:run`.

### Building Native Programs

To build a native application for your operating system, run `mvn clean install`.  This will generate an installer
appropriate for your system, such as an MSI on Windows.

### Official Releases

We also provide periodic releases that include the native programs for the major operating systems.  See the 
Releases page for more details.

## FAQ

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
   an issue on GitHub.
   
4. **How can I use J3 in my application?**  J3 can be used by any program to view high-dimensional data sets.  There are
   several options.  If using Java, you can launch the GUI directly:
   
   ```java
   
       GUI.main(new String[] { "input.csv" });
   ```
   
   From other programming languages, run the `J3` program as a separate process.  For example, with Python we can run:
   
   ```python
   
        import os

        os.environ['PATH'] += os.pathsep + r"C:\Users\J3Dev\Desktop\J3"
        os.system("J3.exe input.csv")
   ```
   