package org.moqui.idea.plugin.util;

import org.moqui.idea.plugin.dom.model.Secas;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;


public final class SecaUtils {
    private SecaUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isSecasFile(@Nullable PsiFile file){
        return MyDomUtils.isSpecialXmlFile(file, Secas.TAG_NAME);
    }


}
