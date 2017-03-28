package org.fife.rsta.ac.java;

import org.fife.rsta.ac.java.rjc.lang.Type;
import org.fife.ui.autocomplete.ParameterizedCompletion;

import java.util.List;

/**
 * @author Zoltán Kuroli
 * @since 2017.03.28.
 */
public class SuperConstructorData implements MemberCompletion.Data {

    private Type enclosingType;
    private List<ParameterizedCompletion.Parameter> parameters;
    private String name;

    public SuperConstructorData(Type enclosingType, List<ParameterizedCompletion.Parameter> parameters)
    {
        this.enclosingType = enclosingType;
        this.parameters = parameters;
        this.name = "super";
    }

    public SuperConstructorData(String name, Type enclosingType, List<ParameterizedCompletion.Parameter> parameters)
    {
        this.enclosingType = enclosingType;
        this.parameters = parameters;
        this.name = name;
    }

    @Override
    public String getEnclosingClassName(boolean fullyQualified)
    {
        return enclosingType.getName(fullyQualified);
    }

    @Override
    public String getSignature()
    {
        StringBuilder sb = new StringBuilder(name);
        sb.append('(');
        int count = parameters.size();
        for (int i=0; i<count; i++) {
            //sb.append(getParameter(i).toString());
            ParameterizedCompletion.Parameter fp = parameters.get(i);
            sb.append(fp.getType());
            sb.append(' ');
            sb.append(fp.getName());
            if (i < count-1) {
                sb.append(", ");
            }
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public String getSummary()
    {
        return getSignature();
    }

    @Override
    public String getType()
    {
        return "void";
    }

    @Override
    public boolean isConstructor()
    {
        return true;
    }

    @Override
    public String getIcon()
    {
        return IconFactory.METHOD_PUBLIC_ICON;
    }

    @Override
    public boolean isAbstract()
    {
        return false;
    }

    @Override
    public boolean isDeprecated()
    {
        return false;
    }

    @Override
    public boolean isFinal()
    {
        return false;
    }

    @Override
    public boolean isStatic()
    {
        return false;
    }
}
