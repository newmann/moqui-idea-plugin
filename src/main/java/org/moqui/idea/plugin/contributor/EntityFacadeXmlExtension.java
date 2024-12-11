package org.moqui.idea.plugin.contributor;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.xml.SchemaPrefix;
import com.intellij.psi.impl.source.xml.TagNameReference;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xml.XmlExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.reference.EntityFacadeXmlTagNameReference;
import org.moqui.idea.plugin.util.EntityFacadeXmlUtils;

import java.util.List;

public class EntityFacadeXmlExtension extends XmlExtension {
    @Override
    public @Nullable TagNameReference createTagNameReference(ASTNode nameElement, boolean startTagFlag) {
        return EntityFacadeXmlTagNameReference.of(nameElement,startTagFlag);
    }

    @Override
    public boolean isAvailable(PsiFile psiFile) {
        return EntityFacadeXmlUtils.isEntityFacadeXmlFile(psiFile);
    }

    @Override
    public @NotNull List<TagInfo> getAvailableTagNames(@NotNull XmlFile xmlFile, @NotNull XmlTag xmlTag) {
        return List.of();
    }

    @Override
    public @Nullable SchemaPrefix getPrefixDeclaration(XmlTag xmlTag, String s) {
        return null;
    }
}
