package org.moqui.idea.plugin.util;

import com.intellij.codeInsight.completion.TagNameReferenceCompletionProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public final class EntityFacadeXmlUtils {
    private EntityFacadeXmlUtils() {
        throw new UnsupportedOperationException();
    }


    public static boolean isEntityFacadeXmlFile(@Nullable PsiFile file){
        if(file == null) return false;
        return MyDomUtils.isSpecialXmlFile(file, EntityFacadeXml.TAG_NAME);
    }

    /**
     * 获取定义在moqui.basic.LocalizedMessage值，还要包括LocalizedMessage，没有别名
     */
//    public static Map<String, XmlTag> getTextTemplateFromFile(@NotNull PsiFile file){
//        if(!isEntityFacadeXmlFile(file)) return new HashMap<>();
//        return PsiTreeUtil.getChildrenOfTypeAsList(file,XmlTag.class).stream()
//                .filter(it-> it.getName().equals("moqui.basic.LocalizedMessage") || it.getName().equals("LocalizedMessage"))
//                .collect(Collectors.toMap(XmlTag::getAttribute("original"), XmlTag));
//
//
//    }
}
