# J3 #

**A free desktop application for producing and sharing high-dimensional, interactive scientific visualizations.**

![Screenshot](http://i.imgur.com/W2zqCTT.jpg)

## What is J3 ##

J3 is an open source, cross-platform application for producing and sharing high-dimensional, interactive scientific
visualizations.  While there are high-quality JavaScript libraries for producing visualization on the web, they are
often plagued by performance issues when dealing with large data sets.  J3, on the other hand, can fluidly support
thousands of data points by leveraging hardware accelerated graphics while simultaneously supporting animations and
interactivity.

## Key Features ##

1. Interactive 2D and 3D plots (scatter, parallel axis, etc.)
2. Various annotations and data callouts
3. Ability to save, share, and load visualizations
4. Create scripted animations (experimental)
5. Extensible design allows adding new widgets

## Get It ##

Pre-packaged distributions are available for Windows, Linux, and Mac.  All distributions include sample data files you can test.

#### Windows ####

Download and extract [J3-Win.zip](https://github.com/MOEAFramework/J3/releases/download/1.0.1/J3-Win.zip) if you
already have Java 8 installed.  Otherwise, download [J3-Win-JRE.zip](https://github.com/MOEAFramework/J3/releases/download/1.0.1/J3-Win-JRE.zip),
which is bundled with the Java 8 runtime environment.  After extracting, run `J3.exe`.  You can also load a data set
from the command line by running `J3.exe <file>`.

You can also download and run [J3.exe](https://github.com/MOEAFramework/J3/releases/download/1.0.1/J3.exe) by
itself, although you will need to provide your own data files.

#### Linux (Debian, Ubuntu, etc.) ####

A deb file is provided to assist installing J3 on Linux.  This installation requires `openjdk-8-jre`.  On
Ubuntu, we needed to add the following repository to satisfy this dependency:

```
    sudo apt-add-repository ppa:openjdk-r/ppa
    sudo apt-get update
```

On some versions of Linux, [JavaFX may not be bundled with OpenJDK](http://stackoverflow.com/questions/34243982/why-is-javafx-is-not-included-in-openjdk-8-on-ubuntu-wily-15-10).  If this is the case, run `sudo apt-get install openjfx` to install JavaFX.

Finally, download and install [J3-1.0-1.deb](https://github.com/MOEAFramework/J3/releases/download/1.0.0/J3_1.0-1.deb).
After installation, J3 will appear as a desktop application.  You can also launch the program by running the command
`J3`.

#### Mac ####

Download and extract [J3-Mac.zip](https://github.com/MOEAFramework/J3/releases/download/1.0.0/J3-Mac.zip) if you
already have Java 8 installed.  Otherwise, download [J3-Mac-JRE.zip](https://github.com/MOEAFramework/J3/releases/download/1.0.0/J3-Mac-JRE.zip),
which is bundled with the Java 8 runtime environment.  After extracting, run `J3.app`.

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
   

## Building ##

J3 now uses Gluon to generate native packages.  See https://docs.gluonhq.com/#_platforms for all pre-requisite software
for each targeted platform.

### Windows

I haven't been successful getting builds to work within Eclipse.  Instead, the commands must be run from the "Developer Command
Prompt for VS20XX":

```
"C:\Program Files (x86)\Microsoft Visual Studio\2019\Enterprise\VC\Auxiliary\Build\vcvars64.bat"
set PATH=%PATH%;C:\apache-maven-3.8.6\bin
set GRAALVM_HOME=C:\graalvm-svm-java17-windows-gluon-22.1.0.1-Final
mvn clean
mvn gluonfx:run
mvn gluonfx:build
mvn gluonfx:package
```

One note: While Eclipse provides an embedded version of Maven, it appears the Maven plugin for Gluon does not like this version.
Instead, install Maven separately and update Eclipse's Maven installations with this copy.

```bash
mvn gluonfx:run                       # run the program
mvn gluonfx:build glouonfx:package    # build and package the native packages
```

See https://github.com/gluonhq/hello-gluon-ci for an example using GitHub Actions to build native packages for major platforms.
