/*
 * 01/12/2017
 *
 * Copyright (C) 2017 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.html;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.HTMLTokenRegistration;

/**
 *
 * @author matta
 */
public class HtmlLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return HTMLTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return HtmlLanguageSupport.class.getName();
    }

}
