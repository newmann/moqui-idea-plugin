package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.Collection;

/**
 * 针对Field进行相关处理
 * 1、获取Field的方式不同，所以需要放到子类中去实现
 */

public abstract class AbstractEntityFieldNameConverter extends ResolvingConverter<AbstractField> implements CustomReferenceConverter {
    protected final java.util.Set<String> ALLOW_PRE_CHARS = java.util.Set.of("+","-","^");

    @Override
    public @Nullable AbstractField fromString(@Nullable @NonNls String s, ConvertContext context) {
        return getField(s, context);
    }

    /**
     * 根据当前位置的字符串，找到对应的Field定义，
     * @param s
     * @param context
     * @return
     */
    abstract AbstractField getField(@Nullable @NonNls String s, ConvertContext context);

    @Override
    public @NotNull Collection<? extends AbstractField> getVariants(ConvertContext context) {
        return getFieldVariants(context);
    }

    /**
     * 根据当前位置，找出所有可选的Field
     * @param context
     * @return
     */
    abstract @NotNull Collection<? extends AbstractField> getFieldVariants(ConvertContext context);

    @Override
    public @Nullable LookupElement createLookupElement(AbstractField field) {
        if (field == null) return super.createLookupElement(field);

        return LookupElementBuilder.create(field,MyDomUtils.getValueOrEmptyString(field.getName()))
                .withCaseSensitivity(false);
    }

    /**
     * 判断当前的Element是否需要进行错误判断，不进行错位判断的就不提示错误
     * @param context 当前的元素
     * @return boolean
     */
    abstract boolean isNotCheckElement(ConvertContext context);

    @Override
    public @Nullable PsiElement getPsiElement(@Nullable AbstractField resolvedValue) {
        if(resolvedValue == null) return null;
        return resolvedValue.getName().getXmlAttributeValue().getOriginalElement();
    }

    @Override
    public @Nullable String toString(@Nullable AbstractField s, ConvertContext context) {
        if (s == null) return null;

        return MyDomUtils.getValueOrEmptyString(s.getName());
    }

    @Override
    public boolean isReferenceTo(@NotNull PsiElement element, String stringValue, @Nullable AbstractField resolveResult, ConvertContext context) {
        if(resolveResult==null) return false;
        return  stringValue.equals(resolveResult.getName().getXmlAttributeValue().getValue());
//        return super.isReferenceTo(element, stringValue, resolveResult, context);
    }

    @Override
    public @NotNull PsiReference[] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        final String valueStr = value.getStringValue();
        if(MyStringUtils.isEmpty(valueStr)) return PsiReference.EMPTY_ARRAY;
        TextRange textRange;
        String fieldName;
        final String firstChar = valueStr.substring(0,1);
        if(ALLOW_PRE_CHARS.contains(firstChar)) {
            fieldName = valueStr.substring(1);
            textRange = new TextRange(2,valueStr.length()+1);
        }else {
            fieldName = valueStr;
            textRange = new TextRange(1,valueStr.length()+1);
        }

        AbstractField field = getField(fieldName,context);
        if (field == null) {return PsiReference.EMPTY_ARRAY;}
        PsiReference[] psiReferences = new PsiReference[1];

        psiReferences[0] = new PsiRef(element,
                textRange,
                field.getName().getXmlAttributeValue());

        return psiReferences;
    }

    @Override
    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
        if(isNotCheckElement(context)) return null;

        if(MyStringUtils.isEmpty(s)) {
            return super.getErrorMessage(s, context);
        }else {
            return "找不到" + s + "的定义";
        }

    }
}
