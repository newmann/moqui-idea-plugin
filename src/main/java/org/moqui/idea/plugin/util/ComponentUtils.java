package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Component;


public final class ComponentUtils {
    private ComponentUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isComponentFile(@Nullable PsiFile file){
        return MyDomUtils.isSpecialXmlFile(file, Component.TAG_NAME);
    }


}
