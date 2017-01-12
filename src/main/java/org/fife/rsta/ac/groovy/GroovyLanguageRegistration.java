/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.groovy;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.GroovyTokenRegistration;

/**
 *
 * @author matta
 */
public class GroovyLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return GroovyTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return GroovyLanguageSupport.class.getName();
    }

}
