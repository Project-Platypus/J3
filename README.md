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

### Native Programs

To build a native application for your operating system, run `mvn clean install`.  This will generate an installer
appropriate for your system, such as an MSI on Windows, inside the `target/` folder.

We also provide pre-compiled releases for Windows (`.msi`), Linux (`.deb`), and MacOS (`.dmg`), available under the Releases page.

## Troubleshooting

As our releases are currently not signed, you might see an error when attempting to run or install the application.

On Windows, you will see a dialog indicating Windows Defender blocked an unrecognized app.  Click "More Info" and "Run Anyways"
to continue the installation.

On MacOS, it will display an error indicating the J3 application is "damaged".  Run the following to remove the quarantine
attribute:

```bash

cd /Applications
sudo xattr -r -d com.apple.quarantine J3.app
```
