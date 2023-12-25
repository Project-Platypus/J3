# J3 - Java 3D Visualization Tool

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
such as [Zulu](https://www.azul.com/downloads).  Pick `JDK FX` under the Java Package filter.  Java 17 or newer
is required.

### Running from Source

If using Eclipse, run the class `j3.GUI` directly or create a new Maven run configuration with
the goal `javafx:run`.

If using Maven, run `mvn javafx:run` from the terminal.

### Building Native Programs

To build a native application for your operating system, run `mvn clean install`.  This will generate an installer
appropriate for your system, such as an MSI on Windows, inside the `target/` folder.

Alternatively, we provide official releases for Windows, Linux, and MacOS under the Releases page.  These releases
include the Java runtime, so there is no need to install Java or JavaFX separately.
