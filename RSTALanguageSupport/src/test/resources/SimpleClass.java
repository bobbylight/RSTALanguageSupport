import java.io.*;
import java.util.List;
import java.util.ArrayList;
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

	private List<Double> list;


	public SimpleClass() {
		list = new ArrayList<Double>();
	}


	/**
	 * Returns a value.
	 *
	 * @return A value.
	 */
	public int getValue() {
		return classInt1;
	}


	private double computeValue(int i, String str) {
		int j = 0;
		try {
			j = new FileReader("fake.txt").read();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return list.get(i) + j + classStr1.hashCode() + random();
	}


	/**
	 * Contains local variables that are a little harder to parse.
	 *
	 * @param newValue
	 * @param unused
	 */
	public void localVarsComplex(String newValue, float unused) {
		int foo = 5;
		double val1 = computeValue(foo, "yes"), val2, val3 = 3f, val4;
		classStr1 = newValue + val1;
		// Lines below just to prevent compiler warnings in Eclipse
		val2 = 0;
		val4 = 5;
		classStr1 += val2 + val3 + val4;
	}


	/**
	 * Contains local variables that are easy to parse.
	 */
	public void localVarsSimple() {
		int temp = classInt1;
		classInt1 = classInt2;
		classInt2 = temp;
		boolean unnecessary = true;
		if (unnecessary) {
			float f = 3.4f;
		}
	}


}