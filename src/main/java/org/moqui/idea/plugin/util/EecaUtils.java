package org.moqui.idea.plugin.util;

import org.moqui.idea.plugin.dom.model.Eecas;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;


public final class EecaUtils {
    private EecaUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isEecasFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file,Eecas.TAG_NAME);

    }


}
