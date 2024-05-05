package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Emecas;


public final class EmecaUtils {
    private EmecaUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isEmecasFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, Emecas.TAG_NAME);

    }


}
