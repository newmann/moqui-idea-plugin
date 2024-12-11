package org.moqui.idea.plugin.reference;


import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.xml.TagNameReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.EntityFacadeXmlTagDescriptor;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class EntityFacadeXmlTagNameReference extends TagNameReference {
    private final String myTagName;
//    private final String myEntityName;

    private final ASTNode myASTNode;
    public EntityFacadeXmlTagNameReference(ASTNode nameElement, boolean startTagFlag) {
        super(nameElement, startTagFlag);
        myTagName = nameElement.getPsi().getText();

//        myEntityName = MyDomUtils.getEntityNameInEntityFacadeXml(nameElement.getPsi()).orElse(null);

        myASTNode = nameElement;
    }

    public static EntityFacadeXmlTagNameReference of(ASTNode nameElement, boolean startTagFlag){
        return new EntityFacadeXmlTagNameReference(nameElement, startTagFlag);
    }

    @Override
    public PsiElement resolve() {
//        if(MyStringUtils.isEmpty(myEntityName)) return super.resolve();
        EntityFacadeXmlTagDescriptor descriptor = EntityFacadeXmlTagDescriptor.of(myASTNode.getPsi());
        if(descriptor.getIsValid()) {
            IndexEntity indexEntity = EntityUtils.getIndexEntityByName(myASTNode.getPsi().getProject(), descriptor.getEntityName()).orElse(null);
            if (indexEntity != null) {
                if (descriptor.getIsRelationship()) {
                    return descriptor.getRelationship().getShortAlias().getXmlAttributeValue();
                } else {
                    if(descriptor.getIsShortAlias()) {
                        return indexEntity.getEntity().getShortAlias().getXmlAttributeValue();
                    }else{
                        return indexEntity.getEntity().getEntityName().getXmlAttributeValue();
                    }
                }
            }
            return null;//报错
        }
        return super.resolve();
    }
    @Override
    protected int getPrefixIndex(@NotNull String name) {
        return name.lastIndexOf(".");
    }

    //    @NotNull
//    @Override
//    public String getCanonicalText() {
//        return MyStringUtils.lowerCaseFirstChar(this.myTextRange.substring(this.myElement.getText()));
//    }
//
//
    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        return ElementManipulators.getManipulator(getElement()).handleContentChange(getElement(),getRangeInElement(),newElementName);
    }
}
