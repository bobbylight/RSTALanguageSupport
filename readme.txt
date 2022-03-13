RSTALanguageSupport Readme
--------------------------
Version 3.0.2
18jan2020

----------------------------------------
Contents
----------------------------------------
I.   Overview
II.  Supported Languages and Status
III. Project Layout
IV.  Building the Jar
V.   Using this Library in your own project
VI.  License
VII. Contributors and thanks


----------------------------------------
I. Overview
----------------------------------------
This project contains "language support" for various programming languages for
RSyntaxTextArea.  "Language support" is a little vague, but it generally means
auto-completion of function names and/or
org.fife.ui.rsyntaxtextarea.Parsers for the language (i.e. squiggle underlining
of errors in the source code).

Documentation is sparse at the moment, but should improve over time.

Like all RSyntaxTextArea-related projects (AutoComplete, SpellChecker), this
project targets Java 8 and beyond.

This library itself relies on the following other libraries (besides
RSyntaxTextArea and AutoComplete):

1. Mozilla Rhino v1.7.6 (http://www.mozilla.org/rhino/)
   Used to implement code completion and syntax checking for JavaScript.


----------------------------------------
II.  Supported Languages and Status
----------------------------------------
Languages with support include:

1. C
   - Auto-completion for the C standard library.
   - Parameter assistance for the C standard library.

2. CSS
   - Auto-completion for selectors (HTML tags only).
   - Auto-completion for CSS properties.
   - Partial auto-completion for CSS property values.

2. Groovy
   - Broken and not very useful at the moment.  Shouldn't be used.

3. HTML
   - Auto-completion for HTML 5.  Suggested attributes are appropriate for the
     current tag.
   - Tag/attribute descriptions are extremely lacking at the moment, assistance
     filling them out would be more than welcome.

4. Java
   - Parser included can parse the JDK6u16 source perfectly (except ...TODO...),
     but has very poor error recovery, so generally only provides a single
     error message per file.  It also doesn't validate method bodies; it is
     designed to only validate class structure, and extract names and types of
     members and local variables (i.e. it only does the stuff useful for code
     completion).
   - Auto-completion, driven from information from the Parser above.  This is
     advanced completion and currently offers suggestions from:
        - Imported classes
        - Methods and fields
        - Local variables (when in a method body)
   - Method parameter assistance; that is, code-complete a method name, and any
     parameters will have a drop-down list of fields and local variables of the
     proper type for that parameter.  This feature is currently very limited
     and only suggests parameters with exactly the right type (no subtypes).
     This will be improved in the next release.
   - Generics support is implemented, but may be buggy.
   - Ctrl+Shift+O (Cmd+Shift+O on OS X) opens "Go to Member" popup.
   - JavaOutlineTree component provides a tree view of a Java file that is
     updated live with edits.

5. JavaScript
   - Embedded Rhino parser for squiggle underlining syntax errors.
   - Auto-completion for variables and functions in the current source file,
     derived from the parser above.  Completion choices are based on the
     type of the variable.  Scope is honored.
   - Ctrl+Shift+O (Cmd+Shift+O on OS X) opens "Go to Member" popup.
   - JavaScriptOutlineTree class provides a tree view of a JavaScript file that
     is updated live with edits.
   - Optional JSHint integration.  If JSHint is installed on your machine, you
     can configure this library to use it for squiggle-underlining of syntax
     errors instead of Rhino.  Point to your own .jshintrc and have full
     control over what issues are flagged.

6. JSP
   - Auto-completion for standard JSP tags.

7. Less
   - Auto-completion for standard less functions as well as CSS constructs.

8. Perl
   - Error checking (squiggle underlining) via using "perl -c ..." on the local
     system.  You can toggle taint mode, etc.
   - Auto-completion for built-in function names.
   - Parameter assistance for those built-in functions.
   - Auto-completion for variable names (suggests only variables that are in
     scope at the current caret position).
   - Can specify/override PERL5LIB when syntax checking.

9. PHP
   - Auto-completion for HTML 5 tags and attributes.  This is inherited
     directly from the HTML support, so improvements there will show up here.
   - Auto-completion for PHP functions.
   - Parameter assistance for PHP functions.

10. Unix Shell
   - Possibly broken at the moment.  Should attempt to use the local man pages
     for descriptions of standard shell commands if the local host is *nix,
     otherwise (on Windows) it defaults to short, generic descriptions.
     Whether local man pages are used is configurable.

11. XML
   - Ctrl+Shift+O (Cmd+Shift+O on OS X) opens "Go to Member" popup.
   - XmlOutlineTree component provides a tree view of an XML file that is
     updated live with edits.



----------------------------------------
III. Project Layout
----------------------------------------

   RSTALanguageSupport/
      src/main/java/                    Source tree
         org/fife/rsta/ac/              Any classes common to all languages
         org/fife/rsta/ac/c             Code completion for C
         org/fife/rsta/ac/demo          Demo app/applet code
         org/fife/rsta/ac/groovy        Code completion for Groovy
         org/fife/rsta/ac/html          Code completion for HTML
         org/fife/rsta/ac/java/**       Code completion for Java
         org/fife/rsta/ac/js/**         Code completion for JavaScript
         org/fife/rsta/ac/less/**       Code completion for Less
         org/fife/rsta/ac/perl          Code completion for Perl
         org/fife/rsta/ac/php           Code completion for PHP
         org/fife/rsta/ac/sh            Code completion fro Unix shell
         org/fife/rsta/ac/xml           Code completion fro XML
      src/main/resources/               Images and resources
      src/test/java/                    Unit tests
      src/test/resources                Resources for unit tests
      build/                            Where gradle generates build artifacts
      data/                             Input XML for some languages
      gradle/                           Gradle wrapper stuff
      build.gradle                      Gradle build script
      gradle.properties                 Properties for the build.
      README.md                         Markdown readme for Git repository
      readme.txt                        This file
      LICENSE.md   License for this library (modified BSD)

Sub-directories of "data/" contain Perl scripts and input files for generating
the XML files used by various languages for code completion.  If you want to
improve the code completion for one of these languages, this is where you have
to work.  But send me any updates you make!  Any improvement, especially to
the method and parameter descriptions, is welcome.



----------------------------------------
IV.  Building the Jar
----------------------------------------
This library uses Gradle for builds.
   
   git clone https://github.com/bobbylight/RSTALanguageSupport.git
   cd ../RSTALanguageSupport
   gradlew build



----------------------------------------
V.   Using this Library in your own project
----------------------------------------
By far, the easiest way to use this library is by simply registering any
RSyntaxTextAreas in your application with the
org.fife.rsta.ac.LanguageSupportFactory.  This is done as follows:

   RSyntaxTextArea textArea = new RSyntaxTextArea(25, 70);
   LanguageSupportFactory.get().register(textArea);

Then, whenever you call textArea.setSyntaxEditingStyle(String style), language
support will automatically be installed as appropriate.  If you set the style
to a language with no language support, then any previous language support will
be removed.  If you set the style to a language with support, any existing
language support will be replaced.

Language support options may vary from language to language.  To tweak the
functionality or appearance for a particular language, you have to edit the
org.fife.rsta.ac.LanguageSupport for that language.  As an example, here is how
you can retrieve the LanguageSupport for Perl:

    LanguageSupportFactory lsf = LanguageSupportFactory.get();
    PerlLanguageSupport support = (PerlLanguageSupport)lsf.
                            getSupportFor(SyntaxConstants.SYNTAX_STYLE_PERL);

Now, you can tweak language-specific options.  For example, PerlLanguageSupport
has a method named "setUseParensWithFunctions(boolean)" that toggles whether
parens are used to wrap parameters inserted via parameter assistance.  This
setting can be toggled to match whether you prefer to "use strict".

Concrete implementations of LanguageSupport are shared amongst all text editors
that are highlighting the same language.  Thus, in the above example, calling
"support.setUseParensWithFunctions(false)" will affect all currently open
instances of RSyntaxTextArea editing Perl, as well as all future instances.



----------------------------------------
VI.  License
----------------------------------------
All code licensed in this library is modified BSD.  See the included
LICENSE.md.

The Rhino jar used for JavaScript support is licensed under the MPL 1.1.  See
the included Rhino.LICENSE.txt for details.

If you use this library in your own projects, please let me know!  I like to
know that my work is useful for others.

Bugs and Feature requests should be posted on GitHub:

   https://github.com/bobbylight/RSTALanguageSupport


----------------------------------------
VII. Contributors and Thanks
----------------------------------------
Thanks to Steve Upton for being incredibly motivated to implement JavScript
support!  It would be nowhere near as complete as it is without him.

Mozilla Rhino is a ridiculously easy library to embed.  I always admire
easy-to-use libraries, and this is definitely a prime example.
