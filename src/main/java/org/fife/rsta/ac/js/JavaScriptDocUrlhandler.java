package org.fife.rsta.ac.js;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;

import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;
import org.fife.rsta.ac.js.completion.JSClassCompletion;
import org.fife.rsta.ac.js.completion.JSCompletion;
import org.fife.rsta.ac.js.completion.JSFieldCompletion;
import org.fife.rsta.ac.js.completion.JSFunctionCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DescWindowCallback;
import org.fife.ui.autocomplete.ExternalURLHandler;
import org.fife.ui.autocomplete.Util;


public class JavaScriptDocUrlhandler implements ExternalURLHandler {

	
	private JavaScriptLanguageSupport languageSupport;
	
	public JavaScriptDocUrlhandler(JavaScriptLanguageSupport languageSupport){
		this.languageSupport = languageSupport;
	}
	/**
	 * Returns the class for a completion (class completions return the class
	 * itself, member completions return the enclosing class).
	 */
	private String getClass(Completion c, String desc) {

		String clazz = null;

		if (c instanceof JSClassCompletion) {
			clazz = ((JSClassCompletion)c).getClassName(true);
		}
		else if (c instanceof JSCompletion) {
			JSCompletion jsc = (JSCompletion)c;
			clazz = jsc.getEnclosingClassName(true);
		}
		else {
			Logger.logError("Can't determine class from completion type: " +
					c.getClass() + " (" + c.toString() + ") - href: " + desc);
		}

		return clazz;

	}
	
	/**
	 * Returns the package of the specified completion.
	 *
	 * @param c The completion.
	 * @param desc The description text being parsed.  Used for errors if we
	 *        cannot determine the package.
	 * @return The package.
	 */
	private String getPackage(Completion c, String desc) {

		String pkg = null;

		if (c instanceof JSClassCompletion) {
			pkg = ((JSClassCompletion)c).getPackageName();
		}
		if (c instanceof JSCompletion) {
			String definedIn = ((JSCompletion)c).getEnclosingClassName(true);
			if (definedIn!=null) {
				int lastDot = definedIn.lastIndexOf('.');
				if (lastDot>-1) {
					pkg = definedIn.substring(0, lastDot);
				}
			}
		}
		else {
			Logger.logError("Can't determine package from completion type: " +
					c.getClass() + " (" + c.toString() + ") - href: " + desc);
		}

		return pkg;

	}
	
	/**
	 * Returns whether the text is a relative URL to other Javadoc.
	 * 
	 * @param text A link in Javadoc.
	 * @return Whether the link is a relative path to more Javadoc.
	 */
	private boolean isRelativeUrl(String text) {
		// Javadoc is always ".html", and we support full URL's elsewhere.
		final String[] EXTS = { ".html", ".htm" };
		for (int i=0; i<EXTS.length; i++) {
			if (text.endsWith(EXTS[i]) || text.indexOf(EXTS[i]+"#")>-1 ||
					text.indexOf(EXTS[i]+"?")>-1) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the parent package <code>backupCount</code> levels up from
	 * the package specified.
	 *
	 * @param pkg A package.
	 * @param backupCount The number of packages "up" to go.
	 * @return The parent package.
	 */
	private String doBackups(String pkg, int backupCount) {
		int lastDot = pkg.length();
		while (lastDot>-1 && backupCount>0) {
			lastDot = pkg.lastIndexOf('.', lastDot);
			backupCount--;
		}
		return lastDot>-1 ? pkg.substring(0, lastDot) : "";
	}


	/**
	 * Returns the Java language support.
	 *
	 * @return The Java language support
	 */
	private JavaScriptLanguageSupport getJavaScriptLanguageSupport() {
		return languageSupport;
	}


	/**
	 * Returns the anchor portion of a (relative) URL.
	 *
	 * @param url The URL.
	 * @return The anchor, or <code>null</code> if none.
	 */
	private static final String getAnchor(String url) {
		int pound = url.indexOf('#');
		return pound>-1 ? url.substring(pound+1) : null;
	}

	/**
	 * Gets the arguments from a method signature.
	 *
	 * @param methodSignature The method signature.
	 * @return The arguments, or an empty array if none.
	 */
	private static final String[] getArgs(String methodSignature) {

		String[] args = null;

		int lparen = methodSignature.indexOf('(');
		if (lparen>-1) {
			int rparen = methodSignature.indexOf(')', lparen); // Should be len-1
			if (rparen>-1 && rparen>lparen+1) {
				String temp = methodSignature.substring(lparen, rparen);
				args = temp.split("\\s*,\\s*");
			}
		}

		if (args==null) {
			args = new String[0];
		}
		return args;

	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void urlClicked(HyperlinkEvent e, Completion c,
							DescWindowCallback callback) {

		// A "real" URL (starts with http://, for example) should be opened
		// in the system browser, not the completion description window.
		URL url = e.getURL();
		if (url!=null) {
			// Try loading in external browser (Java 6+ only).
			try {
				Util.browse(new URI(url.toString()));
			} catch (/*IO*/URISyntaxException ioe) {
				UIManager.getLookAndFeel().provideErrorFeedback(null);
				ioe.printStackTrace();
			}
			return;
		}

		// A relative path URL (no leading "http://") results in a null URL.
		// Class should be in the same package as the one we're currently
		// viewing.  Example:  java.lang.String class documentation
		String desc = e.getDescription();
		Logger.log(desc);
		if (desc!=null) {

			if (isRelativeUrl(desc)) {
				int ext = desc.indexOf(".htm");
				if (ext>-1) {
	
					// Could be <a href="Character.html#section"> link.  A
					// popular href format is "../../util/Formatter.html#syntax".
					// We must determine "relative" package location.
					String anchor = getAnchor(desc);
					String clazz = desc.substring(0, ext);
					int backups = 0;
					while (clazz.startsWith("../")) {
						backups++;
						clazz = clazz.substring(3);
					}
					clazz = clazz.replace('/', '.');

					String pkg = getPackage(c, desc);
					if (pkg!=null) {
						clazz = doBackups(pkg, backups) + "." + clazz;
						JavaScriptLanguageSupport jls = getJavaScriptLanguageSupport();
						ClassFile cf = jls.getJarManager().getClassEntry(clazz);
						if (cf!=null) {
							JSClassCompletion cc = new JSClassCompletion(c.getProvider(), cf, true);
							callback.showSummaryFor(cc, anchor);
						}
					}
	
				}
			}

			// Could be format "com.mycompany.pkg.MyClass", with optional
			// #method() (for example, @see's).
			else {

				JavaScriptLanguageSupport jls = getJavaScriptLanguageSupport();

				String clazz = desc;
				String member = null;
				int pound = desc.indexOf('#');
				if (pound>-1) { // TODO: Handle properly
					member = clazz.substring(pound+1);
					clazz = clazz.substring(0, pound);
				}

				// Just a class name, i.e. "String", "java.util.regex.Pattern".
				if (member==null) {
					boolean guessedPackage = false;
					if (clazz.indexOf('.')==-1) {
						String pkg = getPackage(c, desc);
						if (pkg!=null) {
							clazz = pkg + "." + clazz;
						}
						guessedPackage = true;
					}
					ClassFile cf = jls.getJarManager().getClassEntry(clazz);
					if (cf==null && guessedPackage) {
						// Wasn't in the same package as "c", try java.lang
						int lastDot = clazz.lastIndexOf('.');
						clazz = "java.lang." + clazz.substring(lastDot+1);
						cf = jls.getJarManager().getClassEntry(clazz);
					}
					if (cf!=null) {
						JSClassCompletion cc = new JSClassCompletion(c.getProvider(), cf, true);
						callback.showSummaryFor(cc, null);
					}
					else {
						UIManager.getLookAndFeel().provideErrorFeedback(null);
						Logger.log("Unknown class: " + clazz);
					}
				}

				// Member specified, such as "String#format(...)",
				// "java.util.regex.Pattern.compile(...)", or "#method()".
				else {

					boolean guessedPackage = false;

					if (pound==0) { // Member of this class (i.e. "#foobar(bas)")
						// "clazz" was incorrect previously in this case
						clazz = getClass(c, desc);
					}
					else { // i.e. "String#CASE_INSENSITIVE_ORDER"
						// If no package specified, assume clazz is in the same
						// package as the currently displayed completion.
						if (clazz.indexOf('.')==-1) {
							String pkg = getPackage(c, desc);
							if (pkg!=null) {
								clazz = pkg + "." + clazz;
							}
							guessedPackage = true;
						}
					}

					ClassFile cf = clazz != null ? jls.getJarManager().getClassEntry(clazz) : null;
					if (cf==null && guessedPackage) {
						// Wasn't in the same package as "c", try java.lang
						int lastDot = clazz.lastIndexOf('.');
						clazz = "java.lang." + clazz.substring(lastDot+1);
						cf = jls.getJarManager().getClassEntry(clazz);
					}
					
					if (cf!=null) {

						Completion memberCompletion = null;
						int lparen = member.indexOf('(');
						if (lparen==-1) { // A field, or method with args omitted
							FieldInfo fi = cf.getFieldInfoByName(member);
							if (fi!=null) { // Try fields first, it's most likely
								memberCompletion = new JSFieldCompletion(c.getProvider(), fi);
							}
							else { // Try methods second
								List<MethodInfo> miList = cf.getMethodInfoByName(member, -1);
								if (miList!=null && miList.size()>0) {
									MethodInfo mi = miList.get(0);// Just show the first if multiple
									memberCompletion = new JSFunctionCompletion(c.getProvider(), mi);
								}
							}
						}

						else {
							String[] args = getArgs(member);
							String methodName = member.substring(0, lparen);
							List<MethodInfo> miList = cf.getMethodInfoByName(methodName, args.length);
							if (miList!=null && miList.size()>0) {
								if (miList.size()>1) {
									// TODO: Pick correct overload based on args
									Logger.log("Multiple overload support not yet implemented");
								}
								else {
									MethodInfo mi = miList.get(0);
									memberCompletion = new JSFunctionCompletion(c.getProvider(), mi);
								}
							}
						}

						if (memberCompletion!=null) {
							callback.showSummaryFor(memberCompletion, null);
						}

					}
					else {
						UIManager.getLookAndFeel().provideErrorFeedback(null);
						Logger.logError("Unknown class: " + clazz +
								" (href: " + desc + ")");
					}

				}

			}

		}

	}


}
