package j3;

import java.util.ArrayList;
import java.util.List;

import j3.colormap.Colormap;
import j3.colormap.ColormapFactory;
import j3.colormap.ColormapProvider;
import j3.widgets.Colorbar;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class ColormapSelector extends Pane {
	
	private ObjectProperty<Colormap> colormap = new ObjectPropertyBase<Colormap>() {

		@Override
		public Object getBean() {
			return ColormapSelector.this;
		}

		@Override
		public String getName() {
			return "colormap";
		}
		
	};
	
	public void setColormap(Colormap colormap) {
		this.colormap.set(colormap);
	}
	
	public Colormap getColormap() {
		return colormap.get();
	}
	
	public ObjectProperty<Colormap> colormapProperty() {
		return colormap;
	}

	public ColormapSelector() {
		VBox root = new VBox();
		List<Button> buttons = new ArrayList<Button>();
		
		for (ColormapProvider provider : ColormapFactory.getInstance().getProviders()) {
			TilePane tiles = new TilePane();
			tiles.setHgap(5);
			tiles.setVgap(5);
			
			for (String name : provider.getNames()) {
				Colormap colormap = ColormapFactory.getInstance().getColormap(name);
				
				Button button = new Button(name);
				button.setGraphic(new ImageView(Colorbar.createImage(colormap, 100, 30, Orientation.HORIZONTAL)));
				button.setContentDisplay(ContentDisplay.TOP);
				button.setOnAction(event -> setColormap(colormap));
				
				buttons.add(button);
				tiles.getChildren().add(button);
			}
			
			TitledPane title = new TitledPane();
			title.setText(provider.getCategory());
			title.setContent(tiles);
			
			root.getChildren().add(title);
		}

		getChildren().add(root);
	}

}
