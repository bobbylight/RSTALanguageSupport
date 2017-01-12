/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.less;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.LessTokenRegistration;

/**
 *
 * @author matta
 */
public class LessLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return LessTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return LessLanguageSupport.class.getName();
    }

}
