package j3;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;

public class ErrorUtils {
	
	private ErrorUtils() {
		super();
	}
	
	public static void showError(String header, Throwable e) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("J3 Error");
		alert.setHeaderText(header);
		alert.setContentText(e.toString());
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}

}
