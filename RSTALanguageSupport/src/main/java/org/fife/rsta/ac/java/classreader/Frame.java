/*
 * 03/21/2010
 *
 * Copyright (C) 2010 Robert Futrell
 * robert_futrell at users.sourceforge.net
 * http://fifesoft.com/rsyntaxtextarea
 *
 * This library is distributed under a modified BSD license.  See the included
 * RSTALanguageSupport.License.txt file for details.
 */
package org.fife.rsta.ac.java.classreader;

import java.util.*;

import org.fife.rsta.ac.java.classreader.attributes.Code;


/**
 * A <code>Frame</code> contains information on a method being decompiled,
 * similar to a Frame as defined in 3.6 of the JVM spec.
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class Frame {

	private Stack<String> operandStack;
	private LocalVarInfo[] localVars;


	/**
	 * Constructor.
	 *
	 * @param code The {@link Code} attribute being decompiled.
	 */
	public Frame(Code code) {

		operandStack = new Stack<>();

		localVars = new LocalVarInfo[code.getMaxLocals()];
		int i = 0;
		MethodInfo mi = code.getMethodInfo();

		// Instance methods have an implicit first parameter of "this".
		if (!mi.isStatic()) {
			localVars[i++] = new LocalVarInfo("this", true);
		}

		// Name the passed-in local vars by their types. longs and doubles
		// take up two slots.
		String[] paramTypes = mi.getParameterTypes();
		for (int paramI = 0; paramI <paramTypes.length; paramI++) {
			String type = paramTypes[paramI];
			if (type.indexOf('.')>-1) { // Class types.
				type = type.substring(type.lastIndexOf('.')+1);
			}
			String name = "localVar_" + type + "_" + paramI;
			localVars[i] = new LocalVarInfo(name, true);
			i++;
			if ("long".equals(type) || "double".equals(type)) {
				i++; // longs and doubles take up two slots.
			}
		}

		// NOTE: Other local vars will still be "null" here!  We need to
		// infer their types from their usage during disassembly/decompilation.
		System.out.println("NOTE: " + (localVars.length-i) + " unknown localVars slots");

	}


	/**
	 * Returns the specified local variable.
	 *
	 * @param index The index of the local variable.
	 * @param defaultType The default type.
	 * @return The local variable.
	 */
	public LocalVarInfo getLocalVar(int index, String defaultType) {
		LocalVarInfo var = localVars[index];
		if (var==null) {
			String name = "localVar_" + defaultType + "_" + index;
			var = new LocalVarInfo(name, false);
			localVars[index] = var;
		}
		else {
			var.alreadyDeclared = true; // May be redundant
		}
		return var;
	}


	/**
	 * Pops a value from this frame.
	 *
	 * @return The value.
	 * @see #push(String)
	 */
	public String pop() {
		return operandStack.pop();
	}


	/**
	 * Pushes a value onto this frame.
	 *
	 * @param value The value to push.
	 * @see #pop()
	 */
	public void push(String value) {
		operandStack.push(value);
	}


	/**
	 * Information about a local variable.
	 */
	public static class LocalVarInfo {

		private String value;
		private boolean alreadyDeclared;

		public LocalVarInfo(String value, boolean alreadyDeclared) {
			this.value = value;
			this.alreadyDeclared = alreadyDeclared;
		}

		public String getValue() {
			return value;
		}

		public boolean isAlreadyDeclared() {
			return alreadyDeclared;
		}

	}


}
