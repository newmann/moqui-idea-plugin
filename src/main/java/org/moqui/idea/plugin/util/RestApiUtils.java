package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Resource;


public final class RestApiUtils {
    private RestApiUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isRestApiFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, Resource.TAG_NAME,Resource.ATTR_NoNamespaceSchemaLocation,Resource.VALUE_NoNamespaceSchemaLocation);
    }


}
