/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.perl;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.PerlTokenRegistration;

/**
 *
 * @author matta
 */
public class PerlLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return PerlTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return PerlLanguageSupport.class.getName();
    }

}
