/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.css;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.CSSTokenRegistration;

/**
 *
 * @author matta
 */
public class CssLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return CSSTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return CssLanguageSupport.class.getName();
    }

}
