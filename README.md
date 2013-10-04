This library contains "language support" for various languages for RSyntaxTextArea.  "Language support" is a little vague, but it generally means auto-completion of function names and/or [Parsers](https://github.com/bobbylight/RSyntaxTextArea/blob/master/src/org/fife/ui/rsyntaxtextarea/parser/Parser.java) for the language (i.e. squiggle-underlining of errors in the source code).

Documentation is sparse for the moment, but should improve over time.

Besides RSyntaxTextArea and AutoComplete, this library depends on [Rhino](http://www.mozilla.org/rhino/).  Rhino is used to implement the code completion and syntax checking for JavaScript.

RSTALanguageSupport is available under a [modified BSD license](https://github.com/bobbylight/RSTALanguageSupport/blob/master/RSTALanguageSupport.License.txt).  For more information, visit [http://fifesoft.com/rsyntaxtextarea](http://fifesoft.com/rsyntaxtextarea).

# Supported Languages and Status

Languages supported in this library include:

* C
    * Auto-completion for the C standard library.
    * Parameter assistance for the C standard library.
* Groovy
    * Broken and not very useful at the moment.
* HTML
    * Auto-completion for HTML 5 tags.  Suggested attributes are appropriate for the current tag.
    * Tab/attribute descriptions are extremely lacking at the moment; assistance filling them out would be more than welcome.
* Java
    * The included parser can currently parse the JDK6uXX source, but has very poor error recovery, so generally only provides a single error message per file (unless errors are very basic).  It does not validate method bodies; only class structure, and extracts the names and types of members and local variables (e.g. it only does the stuff useful for code completion).
    * Auto-completion, driven from information from the Parser above.  This is advanced completion and currently offers suggestions from:
        * Imported classes
        * Methods and fields.
        * Local variables (when in a method body)
    * Method parameter assistance; that is, code-complete a method name, and any parameters will have a drop-downl ist of fields and local variables of the proper type for that parameter.  This feature is currently very limited.
    * Ctrl+Shift+O (Cmd+Shift+O on OS X) opens a "Go to Member" popup, a la Eclipse.
* JavaScript
    * Squiggle-underlining of syntax errors, thanks to Rhino.
    * Code competion for variables and functions in the current source file, derived from the parser above.  Completion choices are based on the type of the variable.  Scope is honored.
    * Ctrl+Shift+O (Cmd+Shift+O on OS X) opens a "Go to Member" popup, a la Eclipse.
* JSP
    * Auto-completion for standard JSP tags as well as HTML 5 tags.
* Perl
    * Error checking (squiggle underlining) via using "perl -c ..." on the local system.  You can toggle taint mode, etc.
    * Auto-completion for build-in function names.
    * Parameter assistance for built-in functions.
    * Auto-completion for variables (only those in scope at the current caret position).
    * You can programmatically specify/override PERL5LIB.
* PHP
    * Auto-completion for HTML 5 tags and attributes, inherited from the HTML support.
    * Code completion for PHP functions.
    * Parameter assistance for PHP functions.
* Unix Shell
    * Possibly broken at the moment.  Should attempt to use the local man pages for descriptions of standard shell commands if the local host is *nix, otherwise (on Windows) it defaults to short, generic descriptions.  The location of the man pages that are used is configurable.
* XML
    * Ctrl+Shift+O (Cmd+Shift+O on OS X) opens a "Go to Member" popup, a la Ecli
pse.

# Sister Projects

* [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) provides syntax highlighting, code folding, and many other features out-of-the-box.
* [AutoComplete](https://github.com/bobbylight/AutoComplete) - Adds code completion to RSyntaxTextArea (or any other JTextComponent).
* [RSTAUI](https://github.com/bobbylight/RSTAUI) - Common dialogs needed by text editing applications: Find, Replace, Go to Line, File Properties.
* [SpellChecker](https://github.com/bobbylight/SpellChecker) - Adds squiggle-underline spell checking to RSyntaxTextArea.

# Getting Help

* Add an issue on GitHub
* Ask in the [project forum](http://fifesoft.com/forum/)
* Check the project's [home page](http://fifesoft.com/rsyntaxtextarea)

