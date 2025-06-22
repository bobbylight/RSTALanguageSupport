package org.fife.rsta.ac.js.ast;


/**
 * Object that represents options associated with JavaScriptDeclaration. These can be used to ask questions:
 * <ol>
 *   <li>Which script the JavaScriptDeclaration belongs to.
 *   <li>Whether the JavaScriptDeclaration supports searchable hyperlinks
 * </ol>
 */
public class TypeDeclarationOptions {
	private String ownerScriptName;
	private boolean supportsLinks;
	private boolean preProcessing;

	public TypeDeclarationOptions(String ownerScriptName, boolean supportsLinks, boolean preProcessing) {
		this.ownerScriptName = ownerScriptName;
		this.supportsLinks = supportsLinks;
		this.preProcessing = preProcessing;
	}

	/**
	 * @return the owner script name.
	 */
	public String getOwnerScriptName() {
		return ownerScriptName;
	}

	/**
	 * set the owner script name.
	 *
	 * @param ownerScriptName The name of the script that owns this type declaration.
	 */
	public void setOwnerScriptName(String ownerScriptName) {
		this.ownerScriptName = ownerScriptName;
	}

	/**
	 * @return whether the type declaration supports hyperlinks.
	 */
	public boolean isSupportsLinks() {
		return supportsLinks;
	}

	/**
	 * set whether the type declaration supports hyperlinks.
	 *
	 * @param supportsLinks Whether the type declaration supports hyperlinks.
	 */
	public void setSupportsLinks(boolean supportsLinks) {
		this.supportsLinks = supportsLinks;
	}

	/**
	 * @return whether the type declaration has been created from a pre-processed script.
	 */
	public boolean isPreProcessing() {
		return preProcessing;
	}

	/**
	 * Set whether the type declaration has been created from a pre-processed script.
	 *
	 * @param preProcessing Whether the script was pre-processed.
	 */
	public void setPreProcessing(boolean preProcessing) {
		this.preProcessing = preProcessing;
	}


}
