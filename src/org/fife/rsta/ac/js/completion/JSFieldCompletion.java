package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.rsta.ac.js.ast.type.TypeDeclarationFactory;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.VariableCompletion;


public class JSFieldCompletion extends VariableCompletion implements
		JSCompletion {

	private JSFieldData fieldData;
	private Field field;


	public JSFieldCompletion(CompletionProvider provider, FieldInfo fieldInfo) {
		super(provider, fieldInfo.getName(), null);
		this.fieldData = new JSFieldData(fieldInfo, ((SourceCompletionProvider) provider).getJarManager());
		this.field = fieldData.getField();
	}


	public String getSummary() {

		String summary = field != null ? field.getDocComment() : getName();

		// If it's the Javadoc for the method...
		if (summary != null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary;

	}


	public Icon getIcon() {
		return fieldData.isStatic() ? IconFactory.getIcon(IconFactory.STATIC_VAR_ICON) : IconFactory.getIcon(IconFactory.GLOBAL_VARIABLE_ICON);
	}


	public int getSortIndex() {
		return STATIC_FIELD_INDEX;
	}

	

	public String getEnclosingClassName(boolean fullyQualified) {
		return fieldData.getEnclosingClassName(fullyQualified);
	}


	public String getAlreadyEntered(JTextComponent comp) {
		String temp = getProvider().getAlreadyEnteredText(comp);
		int lastDot = JavaScriptHelper
				.findLastIndexOfJavaScriptIdentifier(temp);
		if (lastDot > -1) {
			temp = temp.substring(lastDot + 1);
		}
		return temp;
	}


	public String getLookupName() {
		return getReplacementText();
	}


	public String getType() {
		return TypeDeclarationFactory.convertJavaScriptType(fieldData.getType(true),
				false);
	}


	public String getType(boolean qualified) {
		return fieldData.getType(true); /*TypeDeclarationFactory.lookupJSType(fieldData.getType(true),
				qualified);*/
	}

}
