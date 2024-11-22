package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

/**
 * 处理ExtendEntity的entity-name属性
 */

public class ExtendEntityNameConverter extends AbstractExtendEntityAttributeConverter {

    @Override
    public @Nullable String getToString(@Nullable Entity entity, ConvertContext context) {
        return MyDomUtils.getXmlAttributeValueString(entity.getEntityName()).orElse(MyStringUtils.EMPTY_STRING);
    }

    @Override
    PsiElement getCreateReferenceElement(@NotNull Entity entity) {
        return entity.getEntityName().getXmlAttributeValue();
    }


}
