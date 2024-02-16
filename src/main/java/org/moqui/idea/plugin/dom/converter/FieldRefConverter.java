package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Field;
import org.moqui.idea.plugin.dom.model.FormList;
import org.moqui.idea.plugin.dom.model.FormSingle;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class FieldRefConverter extends ResolvingConverter<Field> implements CustomReferenceConverter {
    @Override
    public @Nullable Field fromString(@Nullable @NonNls String s, ConvertContext context) {
        if(s == null) return null;
        return getTransition(s,context)
                .orElse(null);

    }

    @Override
    public @NotNull Collection<? extends Field> getVariants(ConvertContext context) {
        return getFieldList(context);
    }

    @Override
    public @Nullable String toString(@Nullable Field transition, ConvertContext context) {
        if (transition == null) return null;
        return transition.getName().getXmlAttributeValue().getValue();
    }

    @Override
    public @Nullable LookupElement createLookupElement(Field transition) {
        if(transition == null) {
            return super.createLookupElement(transition);
        }else {
            String s = transition.getName().getXmlAttributeValue().getValue();
            return LookupElementBuilder.create(s)
                    .withCaseSensitivity(false);

        }
    }

    @Override
    public @Nullable PsiElement getPsiElement(@Nullable Field resolvedValue) {
        return super.getPsiElement(resolvedValue);
    }

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        String related = value.getStringValue();
        if (related == null) return PsiReference.EMPTY_ARRAY;

        Optional<Field> optField = getTransition(related,context);
        if (optField.isEmpty()) return PsiReference.EMPTY_ARRAY;

        final Field field = optField.get();
        PsiReference[] psiReferences = new PsiReference[1];
        psiReferences[0] = new PsiRef(element,
                new TextRange(1,
                        related.length()+1),
                field.getName().getXmlAttributeValue());

        return psiReferences;

    }
    /**
     * 根据当前位置找到所有可用的Transition
     *
     * @param context
     * @return
     */
    private List<Field> getFieldList(ConvertContext context) {

        List<Field> result = new ArrayList<>();


        FormSingle formSingle = ScreenUtils.getCurrentFormSingle(context).orElse(null);
        if(formSingle == null){
            //再判断FormList
            FormList formList = ScreenUtils.getCurrentFormList(context).orElse(null);
            if(formList != null){
                result.addAll(formList.getFieldList());
            }
        }else {
            result.addAll(formSingle.getFieldList());
        }
        return result;
    }
    /**
     * 根据当前位置对应的Relationship
     * @param related
     * @param context
     * @return
     */
    private Optional<Field> getTransition(String related, ConvertContext context) {
        List<Field> fieldList = getFieldList(context);
        return fieldList.stream().filter(
                item->{
                    String str = MyDomUtils.getXmlAttributeValueString(item.getName().getXmlAttributeValue())
                            .orElse(MyStringUtils.EMPTY_STRING);
                    return str.equals(related);
                    }
        ).findFirst();

    }


}
