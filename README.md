# J3 #

J3 is a JavaFX-based library for producing and sharing high-quality,
interactive scientific visualizations.  J3 is centered around the idea of
laying out graphical "widgets" on a canvas.  For example, some
widgets we have in mind include:

1. 2D plots (scatter, histogram, parallel coordinates, etc.)
2. 3D plots (scatter, surface, etc.)
3. Annotations (text, arrows, data inspectors)
4. Custom widgets (via a scripting language like Groovy, Jython, etc.)
5. Animation controls (e.g., play a sequence of animations)

Widgets are backed by a common data representation.  This includes a lightweight
"data frame" implementation for representing arbitrary but type-safe data,
colormaps, etc.  Using JavaFX's properties API, widgets can "bind" to and react
to changes in the underlying data model.  For example, a widget can display an
arrow to the corresponding data point in a scatter plot.  By binding the widget
to the data point, it can automatically update the arrow's location whenever the
data point moves.

We also have plans to enable sharing the J3 visualizations.  Each widget will be
responsible for serializing and deserializing its state.  Then, we can export
the J3 canvas to a file, which can later be imported by another user to recreate
the exact same view.  Similar methods could potentially enable "live streaming".