package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.AbstractForm;
import org.moqui.idea.plugin.dom.model.Field;
import org.moqui.idea.plugin.reference.MoquiBaseReference;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public class FieldRefConverter extends ResolvingConverter<Field> implements CustomReferenceConverter<Field> {
    @Override
    public @Nullable Field fromString(@Nullable @NonNls String s, ConvertContext context) {
        if(s == null) return null;
        return getField(s,context)
                .orElse(null);

    }

    @Override
    public @NotNull Collection<? extends Field> getVariants(ConvertContext context) {
        return getFieldList(context);
    }

    @Override
    public @Nullable String toString(@Nullable Field transition, ConvertContext context) {
        if (transition == null) return null;
        return MyDomUtils.getValueOrEmptyString(transition.getName());
    }

    @Override
    public @Nullable LookupElement createLookupElement(Field transition) {
        if(transition == null) {
            return super.createLookupElement(transition);
        }else {
            String s = MyDomUtils.getValueOrEmptyString(transition.getName());
            return LookupElementBuilder.create(s)
                    .withCaseSensitivity(false);

        }
    }

//    @Override
//    public @Nullable PsiElement getPsiElement(@Nullable Field resolvedValue) {
//        return super.getPsiElement(resolvedValue);
//    }

    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        String related = value.getStringValue();
        if (related == null) return PsiReference.EMPTY_ARRAY;

        Optional<Field> optField = getField(related,context);
        if (optField.isPresent()) {
            final Field field = optField.get();
            return MoquiBaseReference.createOneRefArray(element,
                    new TextRange(1,
                            related.length()+1),
                    field.getName().getXmlAttributeValue());
        }else {
            return MoquiBaseReference.createNullRefArray(element,
                    new TextRange(1,
                            related.length()+1));

        }
    }
    /**
     * 根据当前位置找到所有可用的Field
     *
     * @param context
     * @return
     */
    private List<Field> getFieldList(ConvertContext context) {

        List<Field> result = new ArrayList<>();
        ScreenUtils.getCurrentAbstractForm(context).ifPresent(abstractForm -> result.addAll(ScreenUtils.getFieldListFromForm(abstractForm)));

//        FormSingle formSingle = ScreenUtils.getCurrentFormSingle(context).orElse(null);
//        if(formSingle == null){
//            //再判断FormList
//            FormList formList = ScreenUtils.getCurrentFormList(context).orElse(null);
//            if(formList != null){
//                result.addAll(ScreenUtils.getFieldListFromForm(formList));
//            }
//        }else {
//            result.addAll(ScreenUtils.getFieldListFromForm(formSingle));
//        }

        return result;
    }
    /**
     * 根据当前位置对应的Field
     * @param related
     * @param context
     * @return
     */
    private Optional<Field> getField(String related, ConvertContext context) {
        List<Field> fieldList = getFieldList(context);
        return fieldList.stream().filter(
                item->{
                    String str = MyDomUtils.getValueOrEmptyString(item.getName());
                    return str.equals(related);
                    }
        ).findFirst();

    }

    @Override
    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
        return new HtmlBuilder()
                .append("根据")
                .append("[" + s + "]")
                .append("找不到对应的Field定义。")
                .toString();
    }
}
