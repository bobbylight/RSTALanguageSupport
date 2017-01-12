/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fife.rsta.ac.php;

import org.fife.rsta.ac.LanguageSupportRegistration;
import org.fife.ui.rsyntaxtextarea.modes.PHPTokenRegistration;

/**
 *
 * @author matta
 */
public class PhpLanguageRegistration implements LanguageSupportRegistration {

    @Override
    public String getLanguage() {
        return PHPTokenRegistration.SYNTAX_STYLE;
    }

    @Override
    public String getLanguageSupportType() {
        return PhpLanguageSupport.class.getName();
    }

}
