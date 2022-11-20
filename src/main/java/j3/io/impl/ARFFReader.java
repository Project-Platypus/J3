package j3.io.impl;

import j3.dataframe.DataFrame;
import j3.dataframe.DoubleAttribute;
import j3.dataframe.Instance;
import j3.dataframe.StringAttribute;
import j3.io.AbstractDataFrameReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ARFFReader extends AbstractDataFrameReader {

	@Override
	public List<String> getFileExtensions() {
		return Arrays.asList("arff");
	}

	@Override
	public String getDescription() {
		return "ARFF Data File";
	}

	@Override
	public DataFrame load(InputStream is) throws IOException {
		DataFrame dataFrame = new DataFrame();

		try (Reader reader = new InputStreamReader(is)) {
			StreamTokenizer tokenizer = new StreamTokenizer(reader);

			tokenizer.resetSyntax();
			tokenizer.wordChars(' ' + 1, '\u00FF');
			tokenizer.commentChar('%');
			tokenizer.whitespaceChars(0, ' ');
			tokenizer.whitespaceChars(',', ',');
			tokenizer.quoteChar('\'');
			tokenizer.quoteChar('\"');
			tokenizer.ordinaryChar('{');
			tokenizer.ordinaryChar('}');
			tokenizer.eolIsSignificant(true);

			// read the @RELATION declaration
			readUntilNextToken(tokenizer);

			if (tokenizer.nextToken() != StreamTokenizer.TT_WORD || !tokenizer.sval.equalsIgnoreCase("@RELATION")) {
				handleError(tokenizer, "expected @RELATION keyword");
			}

			if (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
				// do nothing, we don't use the relation name here
			} else {
				handleError(tokenizer, "expected @RELATION value");
			}

			// read the @ATTRIBUTE declarations
			while (true) {
				readUntilNextToken(tokenizer);

				if (tokenizer.nextToken() != StreamTokenizer.TT_WORD
						|| !tokenizer.sval.equalsIgnoreCase("@ATTRIBUTE")) {
					break;
				}

				String attributeName = null;
				String datatype = null;
				List<String> categories = new ArrayList<String>();

				if (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
					attributeName = tokenizer.sval;
				} else {
					handleError(tokenizer, "expected @ATTRIBUTE name");
				}

				if (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {
					datatype = tokenizer.sval;
				} else if (tokenizer.ttype == '{') {
					datatype = "CATEGORY";
					categories.clear();

					// read in the categories
					while (tokenizer.nextToken() != '}') {
						if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
							categories.add(tokenizer.sval);
						} else if (tokenizer.ttype == StreamTokenizer.TT_EOL) {
							handleError(tokenizer, "encountered end of line while reading categories");
						} else if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
							handleError(tokenizer, "encountered end of file while reading categories");
						}
					}
				} else {
					handleError(tokenizer, "expected @ATTRIBUTE datatype");
				}

				if (datatype.equalsIgnoreCase("INTEGER") || datatype.equalsIgnoreCase("REAL")
						|| datatype.equalsIgnoreCase("NUMERIC")) {
					dataFrame.addAttribute(new DoubleAttribute(attributeName));
				} else if (datatype.equalsIgnoreCase("STRING") || datatype.equalsIgnoreCase("CATEGORY")) {
					dataFrame.addAttribute(new StringAttribute(attributeName));
				} else if (datatype.equalsIgnoreCase("DATE")) {
					handleError(tokenizer, "date type is not supported");
				} else {
					handleError(tokenizer, "unknown datatype %s", datatype);
				}
			}

			// read the @DATA declaration
			while (true) {
				readUntilNextToken(tokenizer);

				if (tokenizer.nextToken() == StreamTokenizer.TT_EOF) {
					break;
				}

				Instance instance = new Instance();
				int index = 0;

				while (tokenizer.ttype == StreamTokenizer.TT_WORD) {
					instance.set(dataFrame.getAttribute(index++), tokenizer.sval);
					tokenizer.nextToken();
				}

				dataFrame.addInstance(instance);
			}
		}

		return dataFrame;
	}

	protected void readUntilNextToken(StreamTokenizer tokenizer) throws IOException {
		while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
			// do nothing
		}

		tokenizer.pushBack();
	}

	protected void handleError(StreamTokenizer tokenizer, String format, Object... values) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("line ");
		sb.append(tokenizer.lineno());
		sb.append(": ");
		sb.append(String.format(format, values));

		throw new IOException(sb.toString());
	}

}
