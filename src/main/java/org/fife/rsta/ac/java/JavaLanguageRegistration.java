/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.java;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.JavaTokenRegistration;

/**
 *
 * @author matta
 */
public class JavaLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return JavaTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return JavaLanguageSupport.class.getName();
    }

}
