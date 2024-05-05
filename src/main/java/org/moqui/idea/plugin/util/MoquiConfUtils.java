package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.MoquiConf;


public final class MoquiConfUtils {
    private MoquiConfUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isMoquiConfFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, MoquiConf.TAG_NAME);
    }


}
