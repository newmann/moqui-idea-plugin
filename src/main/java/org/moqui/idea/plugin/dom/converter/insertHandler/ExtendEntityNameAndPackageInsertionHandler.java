package org.moqui.idea.plugin.dom.converter.insertHandler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomManager;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractEntity;
import org.moqui.idea.plugin.dom.model.ExtendEntity;

public class ExtendEntityNameAndPackageInsertionHandler implements InsertHandler<LookupElement>{
    public static final InsertHandler<LookupElement> INSTANCE = new ExtendEntityNameAndPackageInsertionHandler();

    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        Object object = item.getObject();
        if(!(object instanceof AbstractEntity completeItem)) return;
        PsiFile contextFile = context.getFile();
        if (!(contextFile instanceof XmlFile xmlFile)) return;
        PsiElement element = xmlFile.findElementAt(context.getStartOffset());
        XmlTag tag = PsiTreeUtil.getParentOfType(element, XmlTag.class);
        if (tag == null) {
            return;
        }
        context.commitDocument();
        ExtendEntity domCoordinates = getExtendEntityFromCurrentTag(context,tag);
        if (domCoordinates == null) {return;}
        setAttributes(completeItem,domCoordinates);
    }

    private static ExtendEntity getExtendEntityFromCurrentTag(@NotNull InsertionContext context, @NotNull XmlTag tag){
        DomElement element = DomManager.getDomManager(context.getProject()).getDomElement(tag);
        if(element instanceof ExtendEntity) {
            return ((ExtendEntity)element);
        }else {
            return null;
        }
    }

    protected void setAttributes(AbstractEntity completionItem,ExtendEntity domCoordinates){
        domCoordinates.getEntityName().setStringValue(completionItem.getEntityName().getStringValue());
        domCoordinates.getPackage().setStringValue(completionItem.getPackage().getStringValue());
    }

}
