package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Eecas;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;


public final class EntityFacadeXmlUtils {
    private EntityFacadeXmlUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isEntityFacadeXmlFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, EntityFacadeXml.TAG_NAME);
    }


}
