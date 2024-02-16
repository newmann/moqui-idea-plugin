package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.MoquiConf;


public final class MoquiConfUtils {
    private MoquiConfUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isConfFile(@Nullable PsiFile file){
        return MyDomUtils.isSpecialXmlFile(file, MoquiConf.TAG_NAME);
    }


}
