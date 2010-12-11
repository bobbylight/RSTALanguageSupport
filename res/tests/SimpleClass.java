package tests;

import java.io.*;
import java.util.List;
import static java.lang.Math.*;


/**
 * A simple class used in JUnit tests to validate the following features
 * of the Java parser when parsing a class file:
 * 
 * <ul>
 *    <li>Import statements</li>
 *    <li>Class variables</li>
 *    <li>Local variables</li>
 *    <li>Method parameters</li>
 *    <li>Member modifiers</li>
 * </li>
 *
 * @author Robert Futrell
 * @version 1.0
 */
public class SimpleClass {

	/**
	 * A member int variable.
	 */
	public int classInt1;

	protected int classInt2;

	/**
	 * A string member variable.
	 */
	/*
	 * This should not interfere with the javadoc for this member.
	 */
	private String classStr1;

	private List<String> list;


	public SimpleClass() {
		list = new ArrayList<String>();
	}


	/**
	 * Returns a value.
	 *
	 * @return A value.
	 */
	public int getValue() {
		return classInt1;
	}


	public void setString(String newValue, float unused) {
		classStr1 = newValue;
	}


	public void swap() {
		int temp = classInt1;
		classInt1 = classInt2;
		classInt2 = temp;
		boolean unnecessary = true;
		if (unnecessary) {
			float f = 3.4f;
		}
	}


}