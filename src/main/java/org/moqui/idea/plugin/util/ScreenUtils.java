package org.moqui.idea.plugin.util;

import org.moqui.idea.plugin.dom.model.Screen;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;


public final class ScreenUtils {
    private ScreenUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isScreenFile(@Nullable PsiFile file){
        return DomUtils.isSpecialXmlFile(file, Screen.TAG_NAME);
    }


}
