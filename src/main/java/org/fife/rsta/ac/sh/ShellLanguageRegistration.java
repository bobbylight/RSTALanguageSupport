/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.sh;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.UnixShellTokenRegistration;

/**
 *
 * @author matta
 */
public class ShellLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return UnixShellTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return ShellLanguageSupport.class.getName();
    }

}
