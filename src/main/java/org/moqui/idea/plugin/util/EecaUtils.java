package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Eecas;


public final class EecaUtils {
    private EecaUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isEecasFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file,Eecas.TAG_NAME,Eecas.ATTR_NoNamespaceSchemaLocation,Eecas.VALUE_NoNamespaceSchemaLocation);

    }


}
