package org.fife.rsta.ac.js.ecma.api.ecma3.functions;

import org.fife.rsta.ac.js.ecma.api.ecma3.JSArray;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSNumber;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSRegExp;
import org.fife.rsta.ac.js.ecma.api.ecma3.JSString;


public interface JSStringFunctions extends JSObjectFunctions {

	/**
	 * <b>function charAt(position)</b> get the nth character from a string.
	 *
	 * @param position The index of the character that should be returned from <b><i>string</i></b>.
	 * @memberOf String
	 * @returns The <i>nth</i> character of <b><i>string</i></b>.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @see #charCodeAt(JSNumber) charCodeAt()
	 * @see #indexOf(JSString, JSNumber) indexOf()
	 * @see #lastIndexOf(JSString, JSNumber) lastIndexOf()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString charAt(JSNumber position);

	/**
	 * <b>function charCodeAt(position)</b> get the nth character code from a string.
	 *
	 * @param position The index of the character whose encoding is to be returned.
	 * @memberOf String
	 * @returns The Unicode encoding of the <i>nth</i> character within <b><i>string</i></b>.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @see #charAt(JSNumber) charAt()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSNumber charCodeAt(JSNumber position);

	/**
	 * <b>function concat(value1, ...)</b> concatenate strings
	 *
	 * @param value one or more values to be concatenated to <b><i>string</i></b>.
	 * @memberOf String
	 * @returns A new string that results from concatenating each argument to a <b><i>string</i></b>.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString concat(JSString value);


	/**
	 * <b>function indexOf(searchString, startPosition)</b> search a string.
	 *
	 * @param searchString  The substring to be search within <b><i>string</i></b>.
	 * @param startPosition Optional start index.
	 * @memberOf String
	 * @returns The position of the first occurrence of <b><i>searchString</i></b>. -1 if not found.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSNumber indexOf(JSString searchString, JSNumber startPosition);


	/**
	 * <b>function lastIndexOf(searchString, startPosition)</b> search a string backward.
	 *
	 * @param searchString  The substring to be search within <b><i>string</i></b>.
	 * @param startPosition Optional start index.
	 * @memberOf String
	 * @returns The position of the last occurrence of <b><i>searchString</i></b>. -1 if not found.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSNumber lastIndexOf(JSString searchString, JSNumber startPosition);

	/**
	 * <b>function localeCompare(otherString)</b> compare one string to another, using locale-specific ordering.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var string;//array of string initialised somewhere
	 * strings.sort(function(a,b){return a.localCompare(b);});
	 * </pre>
	 *
	 * @param otherString A <b><i>string</i></b> to be compared, in a locale-sensitive fashion, with <b><i>string</i></b>.
	 * @memberOf String
	 * @returns A number that indicates the result of the comparison.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSNumber localeCompare(JSString otherString);

	/**
	 * <b>function match(regexp)</b> find one or more regular-expression matches
	 *
	 * @param regexp A RegExp object that specifies the pattern to be matched.
	 * @memberOf String
	 * @returns An Array containing results of the match.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString match(JSRegExp regexp);

	/**
	 * <b>function replace(regexp, replaceValue)</b> replace substring(s) matching a regular expression.
	 *
	 * @param regexp       A RegExp object that specifies the pattern to be replaced.
	 * @param replaceValue A string that specifies the replacement text.
	 * @memberOf String
	 * @returns {String}
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString replace(JSRegExp regexp, JSString replaceValue);

	/**
	 * <b>function search(regexp)</b> search for a regular expression
	 *
	 * @param regexp A RegExp object that specifies the pattern to be searched.
	 * @memberOf String
	 * @returns The position of the start of the first substring of <b><i>string</i></b>. -1 if no match is found.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSNumber search(JSRegExp regexp);

	/**
	 * <b>function slice(start, end)</b> extract a substring.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * var s = "abcdefg";
	 * s.slice(0,4); //returns "abcd"
	 * s.slice(2,4); //returns "cd"
	 * s.slice(4); //returns "efg"
	 * s.slice(3, -1); //returns "def"
	 * s.slice(3,-2); //returns "de"
	 * </pre>
	 *
	 * @param start The start index where the slice if to begin.
	 * @param end   Optional end index where the slice is to end.
	 * @memberOf String
	 * @returns A new string that contains all the characters of <b><i>string</i></b> from and including <b><i>start</i></b> up to the <b><i>end</i></b>.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString slice(JSNumber start, JSNumber end);

	/**
	 * <b>function split(separator, limit)</b> break a string into an array of strings.
	 * <p>
	 * <strong>Example</strong>
	 * <pre>
	 * "1|2|3|4".split("|"); //returns ["1","2","3","4"]
	 * "%1%2%3%4%".split("%"); //returns ["","1","2","3","4",""]
	 * </pre>
	 *
	 * @param separator The string or regular expression at which the <b><i>string</i></b> splits
	 * @param limit     Optional value that specifies the maximum length of the returned array.
	 * @memberOf String
	 * @returns An array of strings, created by splitting <b><i>string</i></b> using the separator.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSArray split(JSString separator, JSNumber limit);


	/**
	 * <b>function substring(from, to)</b> return a substring of a string.
	 *
	 * @param from The index where to start the extraction. First character is at index 0
	 * @param to   Optional. The index where to stop the extraction. If omitted, it extracts the rest of the string
	 * @memberOf String
	 * @returns A new string of length <b><i>from-to</i></b> which contains a substring of <b><i>string</i></b>.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString substring(JSNumber from, JSNumber to);

	/**
	 * <b>function toLowerCase()</b> Converts a string to lower case.
	 *
	 * @memberOf String
	 * @returns A copy of <b><i>string</i></b> converted to lower case.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @see #charAt(JSNumber) charAt()
	 * @see #indexOf(JSString, JSNumber) indexOf()
	 * @see #lastIndexOf(JSString, JSNumber) lastIndexOf()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString toLowerCase();

	/**
	 * <b>function toLocaleLowerCase()</b> Converts a string to lower case.
	 *
	 * @memberOf String
	 * @returns A copy of <b><i>string</i></b> converted to lower case a locale-specific way.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @see #toLocaleUpperCase()
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString toLocaleLowerCase();

	/**
	 * <b>function toUpperCase()</b> Converts a string to upper case.
	 *
	 * @memberOf String
	 * @returns A copy of <b><i>string</i></b> converted to upper case.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString toUpperCase();

	/**
	 * <b>function toLocaleUpperCase()</b> Converts a string to upper case.
	 *
	 * @memberOf String
	 * @returns A copy of <b><i>string</i></b> converted to upper case a locale-specific way.
	 * @see org.fife.rsta.ac.js.ecma.api.ecma3.JSString String
	 * @since Standard ECMA-262 3rd. Edition
	 * @since Level 2 Document Object Model Core Definition.
	 */
	JSString toLocaleUpperCase();
}
