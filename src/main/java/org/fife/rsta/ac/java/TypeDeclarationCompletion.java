package org.fife.rsta.ac.java;

import org.fife.rsta.ac.java.rjc.ast.NormalInterfaceDeclaration;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.ParameterizedCompletion;

import javax.swing.*;
import java.awt.*;

/**
 * Class to provide completion for inner type declarations
 */
public class TypeDeclarationCompletion extends AbstractJavaSourceCompletion {

    private TypeDeclaration typeDeclaration;

    public TypeDeclarationCompletion(CompletionProvider provider, TypeDeclaration typeDeclaration)
    {
        super(provider, typeDeclaration.getName(false));
        this.typeDeclaration = typeDeclaration;
    }

    @Override
    public int compareTo(Completion c2) {
        if (c2 == this) {
            return 0;
        }
        // Check for classes with same name, but in different packages
        else if(c2.toString().equalsIgnoreCase(toString())) {
            if (c2 instanceof TypeDeclarationCompletion) {
                TypeDeclarationCompletion cc2 = (TypeDeclarationCompletion)c2;
                return getClassName(true).compareTo(cc2.getClassName(true));
            }
        }
        return super.compareTo(c2);
    }

    private String getClassName(boolean fullyQualified) {
        return typeDeclaration.getName(fullyQualified);
    }


    @Override
    public boolean equals(Object obj) {
        return (obj instanceof ClassCompletion) &&
                ((ClassCompletion)obj).getReplacementText().equals(getReplacementText());
    }

    public String getPackageName() {
        return typeDeclaration.getPackage().getName();
    }

    @Override
    public Icon getIcon()
    {
        boolean isInterface = false;
        boolean isPublic = false;
        //boolean isProtected = false;
        //boolean isPrivate = false;
        boolean isDefault = false;

        if (typeDeclaration instanceof NormalInterfaceDeclaration) {
            isInterface = true;
        }
        else if (typeDeclaration.getModifiers().isPublic()) {
            isPublic = true;
        }
        else {
            isDefault = true;
        }

        IconFactory fact = IconFactory.get();
        String key = null;

        if (isInterface) {
            if (isDefault) {
                key = IconFactory.DEFAULT_INTERFACE_ICON;
            }
            else {
                key = IconFactory.INTERFACE_ICON;
            }
        }
        else {
            if (isDefault) {
                key = IconFactory.DEFAULT_CLASS_ICON;
            }
            else if (isPublic) {
                key = IconFactory.CLASS_ICON;
            }
        }

        return fact.getIcon(key, typeDeclaration.isDeprecated());
    }


    @Override
    public String getSummary() {

        if (typeDeclaration.getDocComment() != null) return Util.docCommentToHtml(typeDeclaration.getDocComment());

        // Default to the fully-qualified class name.
        return typeDeclaration.getName(true);
    }


    @Override
    public String getToolTipText() {
        return "class " + getReplacementText();
    }


    @Override
    public int hashCode() {
        return getReplacementText().hashCode();
    }

    @Override
    public void rendererText(Graphics g, int x, int y, boolean selected) {
        String s = typeDeclaration.getName(false);
        g.drawString(s, x, y);
        FontMetrics fm = g.getFontMetrics();
        int newX = x + fm.stringWidth(s);
        if (typeDeclaration.isDeprecated()) {
            int midY = y + fm.getDescent() - fm.getHeight()/2;
            g.drawLine(x, midY, newX, midY);
        }
        x = newX;

        s = " - ";
        g.drawString(s, x, y);
        x += fm.stringWidth(s);

        String pkgName = typeDeclaration.getName(true);
        int lastIndexOf = pkgName.lastIndexOf('.');
        if (lastIndexOf != -1) { // Class may not be in a package
            pkgName = pkgName.substring(0, lastIndexOf);
            Color origColor = g.getColor();
            if (!selected) {
                g.setColor(Color.GRAY);
            }
            g.drawString(pkgName, x, y);
            if (!selected) {
                g.setColor(origColor);
            }
        }
    }
}
