/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.jsp;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.JSPTokenRegistration;

/**
 *
 * @author matta
 */
public class JspLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return JSPTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return JspLanguageSupport.class.getName();
    }

}
