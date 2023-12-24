package j3;

public class Launch {
	
	public static void main(String[] args) {
		// Note: Having a separate class that launches the application appears necessary to avoid
		// errors about missing JavaFX Runtime when executing from the JAR.
		GUI.main(args);
	}

}
