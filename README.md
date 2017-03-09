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

Pre-packaged distributions are available for Windows and Linux.  Distributions for Mac will be made available shortly.
All distributions include sample data files you can test.

### Windows ###

Download and extract [J3-Win.zip](https://github.com/MOEAFramework/J3/releases/download/1.0.0/J3-Win.zip) if you
already have Java 8 installed.  Otherwise, download [J3-Win-JRE.zip](https://github.com/MOEAFramework/J3/releases/download/1.0.0/J3-Win-JRE.zip),
which is bundled with the Java 8 runtime environment.  After extracting, run `J3.exe`.  You can also load a data set
from the command line by running `J3.exe <file>`.

### Linux (Debian, Ubuntu, etc.) ###

A deb file is provided to assist installation of J3 on Linux.  This installation requires `openjdk-8-jre`.  On
Ubuntu, we need to add the following repository to satisfy this dependency:

```

    sudo apt-add-repository ppa:openjdk-r/ppa
    sudo apt-get update
```

Finally, download and install [J3-0.1-1.deb](https://github.com/MOEAFramework/J3/releases/download/1.0.0/J3_1.0-1.deb).

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

## Building ##

J3 uses Maven to manage dependencies.  Use `mvn package` to compile the J3 JAR file.  To create platform-specific
bundles, call the appropriate Ant task.  For example:

```

    mvn package
    ant build-win
    ant build-mac

    # Requires running on Linux (Debian, Ubuntu, etc.) with openjdk-8-jdk installed
    export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-i386/
    ant build-deb
```
