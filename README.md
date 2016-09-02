# J3 #

J3 is a JavaFX-based library for scientific visualization, with a particular
emphasis on viewing large, high-dimensional datasets.

J3 is separated into several core features:

1. A high performance data frame class for storing the dataset along with 
   specific metadata for plotting (e.g., brushing).
   
2. A collection of extensible 3D plots.  We want to be unique by
   offering high-performance, polished views.  This includes adding
   animations where appropriate to provide smooth transitions between changes
   to the plotting controls.

3. Bindings between the plots so that changes in one plot (e.g., selection,
   brushing) propagates efficiently to others.