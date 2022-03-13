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
		for (int param_i=0; param_i<paramTypes.length; param_i++) {
			String type = paramTypes[param_i];
			if (type.indexOf('.')>-1) { // Class types.
				type = type.substring(type.lastIndexOf('.')+1);
			}
			String name = "localVar_" + type + "_" + param_i;
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


	public String pop() {
		return operandStack.pop();
	}


	public void push(String value) {
		operandStack.push(value);
	}


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
