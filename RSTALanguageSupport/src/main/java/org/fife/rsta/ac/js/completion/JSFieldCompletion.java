package org.fife.rsta.ac.js.completion;

import javax.swing.Icon;
import javax.swing.text.JTextComponent;

import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.js.IconFactory;
import org.fife.rsta.ac.js.JavaScriptHelper;
import org.fife.rsta.ac.js.SourceCompletionProvider;
import org.fife.ui.autocomplete.Completion;
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
		setRelevance(fieldData);
	}

	private void setRelevance(JSFieldData data) {
		if (data.isStatic()) {
			setRelevance(STATIC_FIELD_RELEVANCE);
		} else  {
			setRelevance(GLOBAL_VARIABLE_RELEVANCE);
		}
	}

	@Override
	public String getSummary() {

		String summary = field != null ? field.getDocComment() : getName();

		// If it's the Javadoc for the method...
		if (summary != null && summary.startsWith("/**")) {
			summary = org.fife.rsta.ac.java.Util.docCommentToHtml(summary);
		}

		return summary;

	}


	@Override
	public Icon getIcon() {
		return fieldData.isStatic() ? IconFactory.getIcon(IconFactory.STATIC_VAR_ICON) :
			fieldData.isPublic() ? IconFactory.getIcon(IconFactory.GLOBAL_VARIABLE_ICON) : IconFactory.getIcon(IconFactory.DEFAULT_VARIABLE_ICON);
	}


	@Override
	public String getEnclosingClassName(boolean fullyQualified) {
		return fieldData.getEnclosingClassName(fullyQualified);
	}


	@Override
	public String getAlreadyEntered(JTextComponent comp) {
		String temp = getProvider().getAlreadyEnteredText(comp);
		int lastDot = JavaScriptHelper
				.findLastIndexOfJavaScriptIdentifier(temp);
		if (lastDot > -1) {
			temp = temp.substring(lastDot + 1);
		}
		return temp;
	}


	@Override
	public String getLookupName() {
		return getReplacementText();
	}


	@Override
	public String getType() {
		return ((SourceCompletionProvider) getProvider()).getTypesFactory().convertJavaScriptType(fieldData.getType(true),
				false);
	}


	@Override
	public String getType(boolean qualified) {
		return fieldData.getType(true); /*TypeDeclarationFactory.lookupJSType(fieldData.getType(true),
				qualified);*/
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof JSFieldCompletion) {
			JSFieldCompletion jsComp = (JSFieldCompletion) obj;
			return getLookupName().equals(jsComp.getLookupName());
		}
		return super.equals(obj);
	}

	@Override
	public int compareTo(Completion o) {
		if (o==this) {
			return 0;
		}
		else if (o instanceof JSFieldCompletion) {
			JSFieldCompletion c2 = (JSFieldCompletion)o;
			return getLookupName().compareTo(c2.getLookupName());
		}
		return super.compareTo(o);
	}

	@Override
	public int hashCode() {
		return getLookupName().hashCode();
	}

}
