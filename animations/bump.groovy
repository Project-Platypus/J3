import j3.Transitions
import j3.Selector
import javafx.scene.shape.Box

def points = Selector.on(canvas).get(Box.class)

Transitions.on(points)
	       .scale(2.5, 400)
	       .waitFor()
	       .scale(-2.5, 400)
	       .play()