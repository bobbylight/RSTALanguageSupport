/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
