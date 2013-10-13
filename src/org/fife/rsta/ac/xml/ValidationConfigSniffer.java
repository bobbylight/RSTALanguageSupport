package org.fife.rsta.ac.xml;

import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;

public class ValidationConfigSniffer {


	public ValidationConfig sniff(RSyntaxDocument doc) {

		ValidationConfig config = null;

		OUTER:
		for (Token token : doc) {
			switch (token.getType()) {
				case TokenTypes.MARKUP_DTD:
					System.out.println("DTD: " + token.getLexeme());
					break OUTER;
				case TokenTypes.MARKUP_TAG_NAME:
					// We only care about the first element
					//System.out.println("First element: " + token.getLexeme());
					break OUTER;
			}
		}

		return config;

	}


}
