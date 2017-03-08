import j3.Transitions
import j3.Selector
import j3.widget.impl.TextWidget
import javafx.scene.text.Text

widget = new TextWidget()

text = Selector.on(widget).getFirst(Text.class)
text.setText("Hello World!")
text.setStyle("-fx-font-size: 36")

canvas.add(widget)
widget.setLayoutX(canvas.getScene().getWidth()/2 - text.getBoundsInLocal().getWidth()/2);
widget.setLayoutY(canvas.getScene().getHeight()/2 - text.getBoundsInLocal().getHeight()/2);