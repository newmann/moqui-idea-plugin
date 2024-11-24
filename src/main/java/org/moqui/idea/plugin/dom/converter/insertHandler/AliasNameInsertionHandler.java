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
import org.moqui.idea.plugin.dom.model.Alias;
import org.moqui.idea.plugin.util.MyDomUtils;

public class AliasNameInsertionHandler implements InsertHandler<LookupElement>{
    public static final InsertHandler<LookupElement> INSTANCE = new AliasNameInsertionHandler();

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        Object object = item.getObject();
        if(!(object instanceof AliasInsertObject aliasInsertObject)) return;

        PsiFile contextFile = context.getFile();
        if (!(contextFile instanceof XmlFile xmlFile)) return;
        PsiElement element = xmlFile.findElementAt(context.getStartOffset());
        XmlTag tag = PsiTreeUtil.getParentOfType(element, XmlTag.class);
        if (tag == null) {
            return;
        }
        context.commitDocument();
        Alias alias = MyDomUtils.getLocalDomElementByPsiElement(element,Alias.class).orElse(null);

        if (alias == null) {return;}
        alias.getEntityAlias().setStringValue(aliasInsertObject.getEntityAlias());

    }


}
