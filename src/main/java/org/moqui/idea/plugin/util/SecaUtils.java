package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Secas;


public final class SecaUtils {
    private SecaUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isSecasFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, Secas.TAG_NAME,Secas.ATTR_NoNamespaceSchemaLocation,Secas.VALUE_NoNamespaceSchemaLocation);
    }


}
