package org.fife.rsta.ac.js.ast.type;

/**
 * Extended TypeDeclaration that stores the TypeDeclaration for the Array.
 * e.g.
 * var a = [1, 2, 3]; //Array Type - Number
 * var b = ["","",""]; //Array Type - String
 * var c = [1, "", true] //Array Type - any (Default)
 * <p>
 * This is used to determine the type of object in the array when setting variables:
 * e.g.
 * var a = [1, 2, 3]; //Array Type - Number
 * var d = a[1]; //var d is resolved as a Number
 *
 */
public class ArrayTypeDeclaration extends TypeDeclaration {

	private TypeDeclaration arrayType;


	public ArrayTypeDeclaration(String pkg, String apiName, String jsName,
			boolean staticsOnly) {
		super(pkg, apiName, jsName, staticsOnly);
	}


	public ArrayTypeDeclaration(String pkg, String apiName, String jsName) {
		super(pkg, apiName, jsName);
	}


	public TypeDeclaration getArrayType() {
		return arrayType;
	}


	public void setArrayType(TypeDeclaration containerType) {
		this.arrayType = containerType;
	}


	@Override
	public boolean equals(Object obj) {
		boolean equals = super.equals(obj);

		if (equals) {
			// check the container types
			ArrayTypeDeclaration objArrayType = (ArrayTypeDeclaration) obj;

			if (getArrayType() == null && objArrayType.getArrayType() == null) {
				return false;
			}

			if (getArrayType() == null && objArrayType.getArrayType() != null) {
				return false;
			}

			if (getArrayType() != null && objArrayType.getArrayType() == null) {
				return false;
			}
			// else
			return getArrayType().equals(
					((ArrayTypeDeclaration) obj).getArrayType());

		}
		return equals;
	}

	@Override
	public int hashCode() {
		int hash = super.hashCode();
		if (arrayType != null) {
			hash = 31 * hash + arrayType.hashCode();
		}
		return hash;
	}
}
