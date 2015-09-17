/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.js;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.JavaScriptTokenRegistration;

/**
 *
 * @author matta
 */
public class JavaScriptLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return JavaScriptTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return JavaScriptLanguageSupport.class.getName();
    }

}
