package org.fife.rsta.ac.js.completion;

import java.util.Iterator;

import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.buildpath.SourceLocation;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.rjc.ast.CompilationUnit;
import org.fife.rsta.ac.java.rjc.ast.Field;
import org.fife.rsta.ac.java.rjc.ast.Member;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;


public class JSFieldData {

	private FieldInfo info;
	private JarManager jarManager;

	public JSFieldData(FieldInfo info, JarManager jarManager)
	{
		this.info = info;
		this.jarManager = jarManager;
	}


	public Field getField()
	{
		ClassFile cf = info.getClassFile();
		SourceLocation loc = jarManager.getSourceLocForClass(
				cf.getClassName(true));
		return getFieldFromSourceLoc(loc, cf);
	}

	/**
	 * Scours the source in a location (zip file, directory), looking for a
	 * particular class's source.  If it is found, it is parsed, and the
	 * {@code Method} for this method (if any) is returned.
	 *
	 * @param loc The zip file, jar file, or directory to look in.
	 * @param cf The {@link ClassFile} representing the class of this method.
	 * @return The method, or <code>null</code> if it cannot be found, or an
	 *         IO error occurred.
	 */
	private Field getFieldFromSourceLoc(SourceLocation loc, ClassFile cf) {

		CompilationUnit cu = org.fife.rsta.ac.java.Util.
									getCompilationUnitFromDisk(loc, cf);

		// If the class's source was found and successfully parsed, look for
		// this method.
		if (cu!=null) {

			Iterator<TypeDeclaration> i = cu.getTypeDeclarationIterator();
			while (i.hasNext()) {

				TypeDeclaration td = i.next();
				String typeName = td.getName();

				// Avoid inner classes, etc.
				if (typeName.equals(cf.getClassName(false))) {

					// Get all overloads of this method with the number of
					// parameters we're looking for.  99% of the time, there
					// will only be 1, the method we're looking for.
					Iterator<Member> j = td.getMemberIterator();
					while (j.hasNext()) {
						Member member = j.next();
						if (member instanceof Field &&
								member.getName().equals(info.getName())) {
							return (Field) member;
						}
					}

				} // if (typeName.equals(cf.getClassName(false)))

			} // for (Iterator i=cu.getTypeDeclarationIterator(); i.hasNext(); )

		} // if (cu!=null)

		return null;

	}

	public String getType(boolean qualified)
	{
		return info.getTypeString(qualified);
	}

	public boolean isStatic()
	{
		return info.isStatic();
	}

	public boolean isPublic()
	{
		int access = info.getAccessFlags();
		return org.fife.rsta.ac.java.classreader.Util.isPublic(access);
	}

	public String getEnclosingClassName(boolean fullyQualified) {
		return info.getClassFile().getClassName(fullyQualified);
	}
}
