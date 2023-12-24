# J3 - 3D Interactive Visualization in Java

## Usage

### Prerequisites

1. Download and install Java 17+
2. Download and install JavaFX SDK - https://gluonhq.com/products/javafx/
3. Set the environment variables `JAVA_HOME` and `JAVAFX_HOME`

### Launch App

```bash

# Windows
java --module-path %JAVAFX_HOME%\lib --add-modules javafx.controls --class-path "lib/*" j3.GUI

# Linux
java --module-path ${JAVAFX_HOME}\lib --add-modules javafx.controls --class-path "lib/*" j3.GUI
```