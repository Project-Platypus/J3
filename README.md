# J3

**A free desktop application for producing and sharing high-dimensional, interactive scientific visualizations.**

![Screenshot](http://i.imgur.com/W2zqCTT.jpg)

## Key Features

1. Interactive 2D and 3D plots (scatter, parallel axis, etc.)
2. Various annotations and data callouts
3. Ability to save, share, and load visualizations
4. Create scripted animations (experimental)
5. Extensible design allows adding new widgets

## Compatibility

The latest version of J3, `2.x`, supports Java 17 and newer.  It requires installing the JavaFX runtime separately
and Gluon / GraalVM for native compilation.

The older versions of J3, `1.x`, require Java 8.  The `1.x` versions are no longer supported.

## Compiling

There are a few options for running J3 that have different requirements.  These build steps are tested on Windows but
should be similar on other platforms.

### Prerequisites

1. Download and install [Maven](https://maven.apache.org/).  Set the `MAVEN_HOME` environment variable to the
   installation path.
2. Download and install the [JavaFX runtime](https://gluonhq.com/products/javafx/).  Set the `JAVAFX_HOME`
   environment variable to the installation path.
   
If building native executables, the following dependencies are also required:

1. Ensure all prerequisites for your target platform(s) are satisfied: https://docs.gluonhq.com/#_platforms
2. Download and install [GraalVM](https://www.graalvm.org/).  Set the `GRAALVM_HOME`
   environment variable to the installation path.

### Option 1 - Eclipse

J3 can be launched directly from Eclipse.  First, create a new Eclipse project from the J3 source code.
Then, right-click on `src/main/java/j3/GUI.java` and run as a Java Application.

Under VM arguments, add:

```
--module-path ${JAVAFX_HOME}\lib --add-modules javafx.controls
```

Apply the changes and run the application.

### Option 2 - Maven

On Windows, we use the Visual Studio Developer Command Prompt to build J3 as it requires a compiler/linker:

```
"C:\Program Files (x86)\Microsoft Visual Studio\2019\Enterprise\VC\Auxiliary\Build\vcvars64.bat"

set MAVEN_HOME=C:\apache-maven-3.8.6
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-18.0.2.101-hotspot
set GRAALVM_HOME=C:\graalvm-svm-java17-windows-gluon-22.1.0.1-Final
set PATH=%MAVEN_HOME%\bin;%JAVA_HOME%\bin;%PATH%

mvn clean
mvn gluonfx:build         # Compile J3
mvn gluonfx:run           # Launch J3
```

### Option 3 - Native Executable

Following the same steps above to build J3 using Maven, run:

```
mvn gluonfx:nativerun     # Run the native version of J3
mvn gluonfx:package       # Package J3 into an installer
```

Note: Some functionality is not supported for native executables, including the Camera and Animation widgets.

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
   