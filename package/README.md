# J3 - 3D Interactive Visualization in Java

## Prerequisites

1. Download and install Java 17+
2. Download and install JavaFX SDK - https://gluonhq.com/products/javafx/
3. Set the environment variables `JAVA_HOME` and `JAVAFX_HOME`

## Usage

Either use the provided `j3` command to run the application or launch from the terminals with:

```bash

# Windows
java --module-path %JAVAFX_HOME%\lib --add-modules javafx.controls --class-path "lib/*" j3.GUI

# Linux
java --module-path ${JAVAFX_HOME}\lib --add-modules javafx.controls --class-path "lib/*" j3.GUI
```