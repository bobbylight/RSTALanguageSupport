/*
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.asm6502;

import org.fife.ui.autocomplete.*;

import java.io.IOException;


/**
 * A completion provider for 6502 assembler code (not comments).
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Asm6502CodeCompletionProvider extends DefaultCompletionProvider {


	/**
	 * Constructor.
	 */
	public Asm6502CodeCompletionProvider() {

	    initCompletions();
	}


    /**
     * Loads completions for the 6502 instruction set.
     */
    private void initCompletions() {
        try {
            loadFromXML("data/asm6502.xml");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
