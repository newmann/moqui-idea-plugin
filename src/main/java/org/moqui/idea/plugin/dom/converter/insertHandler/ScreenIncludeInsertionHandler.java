package org.moqui.idea.plugin.dom.converter.insertHandler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.SectionInclude;
import org.moqui.idea.plugin.dom.model.TransitionInclude;
import org.moqui.idea.plugin.util.MyDomUtils;

/**
 * 处理ScreenInclude的insert，包括TransitionInclude和SectionInclude
 */
public class ScreenIncludeInsertionHandler implements InsertHandler<LookupElement>{
    public static final InsertHandler<LookupElement> INSTANCE = new ScreenIncludeInsertionHandler();

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        Object object = item.getObject();
        if(!(object instanceof ScreenIncludeInsertObject transitionIncludeInsertObject)) return;

        PsiFile contextFile = context.getFile();
        if (!(contextFile instanceof XmlFile xmlFile)) return;
        PsiElement element = xmlFile.findElementAt(context.getStartOffset());
        XmlTag tag = PsiTreeUtil.getParentOfType(element, XmlTag.class);
        if (tag == null) {
            return;
        }
        context.commitDocument();
        TransitionInclude transitionInclude = MyDomUtils.getLocalDomElementByPsiElement(element,TransitionInclude.class).orElse(null);

        if (transitionInclude != null) {
            transitionInclude.getName().setStringValue(transitionIncludeInsertObject.getObjectName());
            transitionInclude.getLocation().setStringValue(transitionIncludeInsertObject.getLocation());
        }else {
            //再判断SectionInclude
            SectionInclude sectionInclude = MyDomUtils.getLocalDomElementByPsiElement(element,SectionInclude.class).orElse(null);

            if (sectionInclude != null) {
                sectionInclude.getName().setStringValue(transitionIncludeInsertObject.getObjectName());
                sectionInclude.getLocation().setStringValue(transitionIncludeInsertObject.getLocation());
            }
        }
    }


}
