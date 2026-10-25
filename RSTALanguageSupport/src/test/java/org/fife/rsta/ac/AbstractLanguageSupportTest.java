/*
 * This library is distributed under a modified BSD license.  See the included
 * LICENSE.md file for details.
 */
package org.fife.rsta.ac;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DescWindowVisibility;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the description window settings in
 * {@code AbstractLanguageSupport}.
 */
class AbstractLanguageSupportTest {

    private static final class TestLanguageSupport extends AbstractLanguageSupport {

        AutoCompletion create() {
            CompletionProvider provider = new DefaultCompletionProvider();
            return createAutoCompletion(provider);
        }

        @Override
        public void install(RSyntaxTextArea textArea) {
            // Not needed for these tests
        }

        @Override
        public void uninstall(RSyntaxTextArea textArea) {
            // Not needed for these tests
        }
    }

    @Test
    void testDefaults() {
        TestLanguageSupport support = new TestLanguageSupport();
        assertEquals(DescWindowVisibility.NEVER, support.getDescWindowVisibility());
        assertEquals(AutoCompletion.getDefaultDescWindowToggleKey(), support.getDescWindowToggleKey());
    }

    @Test
    void testCreateAutoCompletion_appliesDescWindowSettings() {

        TestLanguageSupport support = new TestLanguageSupport();
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK);
        support.setDescWindowVisibility(DescWindowVisibility.ON_DEMAND);
        support.setDescWindowToggleKey(key);

        AutoCompletion ac = support.create();
        assertEquals(DescWindowVisibility.ON_DEMAND, ac.getDescWindowVisibility());
        assertEquals(key, ac.getDescWindowToggleKey());
    }

    @Test
    void testSetDescWindowVisibility_roundTrips() {
        TestLanguageSupport support = new TestLanguageSupport();
        for (DescWindowVisibility visibility : DescWindowVisibility.values()) {
            support.setDescWindowVisibility(visibility);
            assertEquals(visibility, support.getDescWindowVisibility());
        }
    }

    @Test
    void testSetDescWindowVisibility_null() {
        TestLanguageSupport support = new TestLanguageSupport();
        assertThrows(NullPointerException.class, () -> support.setDescWindowVisibility(null));
    }

    @Test
    void testSetDescWindowToggleKey_null() {
        TestLanguageSupport support = new TestLanguageSupport();
        support.setDescWindowToggleKey(null);
        assertNull(support.getDescWindowToggleKey());
    }
}
