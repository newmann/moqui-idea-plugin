package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.ConvertContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;

/**
 * 对应到ExtendEntity的package处理
 */

public class ExtendEntityPackageConverter extends AbstractExtendEntityAttributeConverter{


    @Override
    @Nullable String getToString(@Nullable Entity entity, ConvertContext context) {
        if(entity == null) {return null;
        }else {
            return entity.getPackage().getStringValue();
        }

    }

    @Override
    PsiElement getCreateReferenceElement(@NotNull Entity entity) {
        return entity.getPackage().getXmlAttributeValue();
    }


}
