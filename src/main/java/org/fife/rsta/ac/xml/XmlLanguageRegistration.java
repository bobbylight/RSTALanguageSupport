/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.xml;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.XMLTokenRegistration;

/**
 *
 * @author matta
 */
public class XmlLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return XMLTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return XmlLanguageSupport.class.getName();
    }

}
