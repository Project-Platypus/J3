package j3.io.impl;

import j3.Canvas;
import j3.io.AbstractCanvasReader;
import j3.io.CanvasReader;
import j3.io.CanvasReaderFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.ChoiceDialog;

public class SmartReader extends AbstractCanvasReader {

	@Override
	public List<String> getFileExtensions() {
		return Arrays.asList("*");
	}

	@Override
	public String getDescription() {
		return "All Files";
	}

	@Override
	public void load(File file, Canvas canvas) throws IOException {
		CanvasReader selectedReader = null;
		List<CanvasReader> readers = new ArrayList<>();
		String extension = FilenameUtils.getExtension(file.getName());

		for (CanvasReader reader : CanvasReaderFactory.getInstance().getProviders()) {
			for (String ext : reader.getFileExtensions()) {
				if (StringUtils.equalsIgnoreCase(ext, extension)) {
					readers.add(reader);
				}
			}
		}

		if (readers.isEmpty()) {
			List<CanvasReader> choices = CanvasReaderFactory.getInstance().getProviders();

			ChoiceDialog<CanvasReader> dialog = new ChoiceDialog<>(choices.get(0), choices);
			dialog.setTitle("Select File Type");
			dialog.setHeaderText(
					"The file type is not recognized.  Please select the appropriate reader from the list below.");
			dialog.setContentText("Reader:");

			Optional<CanvasReader> result = dialog.showAndWait();

			if (result.isPresent()) {
				selectedReader = result.get();
			}
		} else if (readers.size() > 1) {
			List<CanvasReader> choices = readers;

			ChoiceDialog<CanvasReader> dialog = new ChoiceDialog<>(choices.get(0), choices);
			dialog.setTitle("Select File Type");
			dialog.setHeaderText(
					"More than one reader is available for the given file type.  Please select the appropriate reader from the list below.");
			dialog.setContentText("Reader:");

			Optional<CanvasReader> result = dialog.showAndWait();

			if (result.isPresent()) {
				selectedReader = result.get();
			}
		} else if (readers.size() == 1) {
			selectedReader = readers.get(0);
		}

		if (selectedReader != null) {
			selectedReader.load(file, canvas);
		}
	}

	@Override
	public void load(InputStream is, Canvas canvas) throws IOException {
		throw new UnsupportedOperationException("not supported, use the file method instead");
	}

}
