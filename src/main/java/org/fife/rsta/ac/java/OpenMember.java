package org.fife.rsta.ac.java;

import org.apache.log4j.Logger;
import org.fife.rsta.ac.java.classreader.ClassFile;
import org.fife.rsta.ac.java.classreader.FieldInfo;
import org.fife.rsta.ac.java.classreader.MethodInfo;

import sun.reflect.Reflection;

public class OpenMember {

	private static final Logger log = Logger.getLogger(Reflection.getCallerClass(1));

	public static OpenMember openMember =new OpenMember();


	public void openClass(ClassFile classFile) {
		log.info(classFile);
	}

	public void openMember(MethodInfo methodInfo) {
		log.info(methodInfo);
	}

	public void openMember(FieldInfo fieldInfoByName) {
		log.info(fieldInfoByName);
	}


}
