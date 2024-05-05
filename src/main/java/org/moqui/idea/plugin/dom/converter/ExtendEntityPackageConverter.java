package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import icons.MoquiIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.converter.insert.ExtendEntityNameAndPackageInsertionHandler;
import org.moqui.idea.plugin.dom.model.AbstractEntity;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import java.util.Collection;
import java.util.Optional;

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
