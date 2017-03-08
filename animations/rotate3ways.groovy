import j3.Transitions
import j3.Selector
import j3.widget.impl.scatter.Axis3D

def axis = Selector.on(canvas).get(Axis3D)

Transitions.on(axis)
           .rotateX(45, 1000)
           .waitFor()
           .rotateY(45, 1000)
           .waitFor()
           .rotateZ(45, 1000)
           .play()