package org.fife.rsta.ac.js.ast;



public abstract class JavaScriptDeclaration {

	private String name;
	private int offset;
	private int start;
	private int end;
	private CodeBlock block;
	private TypeDeclarationOptions options;


	public JavaScriptDeclaration(String name, int offset, CodeBlock block) {
		this.name = name;
		this.offset = offset;
		this.block = block;
	}

	/**
	 * @return Name of the declaration
	 */
	public String getName() {
		return name;
	}


	/**
	 * @return variable position within the script
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Gets the end offset of this declaration.
	 *
	 * @return The end offset.
	 */
	public int getEndOffset() {
		return end;
	}

	/**
	 * Sets the end offset of this declaration.
	 *
	 * @param end the end offset.
	 * @see #getEndOffset()
	 */
	public void setEndOffset(int end) {
		this.end = end;
	}


	/**
	 * Sets the start offset of this declaration.
	 *
	 * @param start the start offset
	 * @see #getStartOffSet()
	 */
	public void setStartOffset(int start) {
		this.start = start;
	}


	/**
	 * Gets the start offset of this declaration.
	 *
	 * @return The start offset.
	 */
	public int getStartOffSet() {
		return start;
	}

	/**
	 * @return codeblock associated with this declaration
	 */
	public CodeBlock getCodeBlock() {
		return block;
	}

	/**
	 * Set the JavaScript options associated with this declaration.
	 * These are used to defined whether options are available to
	 *
	 * @param options
	 */
	public void setTypeDeclarationOptions(TypeDeclarationOptions options) {
		this.options = options;
	}

	/**
	 * @return the  JavaScript options associated with this declaration
	 */
	public TypeDeclarationOptions getTypeDeclarationOptions() {
		return options;
	}


}
