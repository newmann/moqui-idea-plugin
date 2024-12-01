package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * OrderBy和SelectField属性进行Complete提示
 * 在OrderBy中，可以字段名前面可以添加字符：
 * + 表示顺序排列
 * - 表示倒序排列
 * ^ 表示大小写敏感
 */
@Deprecated
public class MultiFieldNameCompletionProvider extends AbstractSimpleCompletionProvider {
    public static MultiFieldNameCompletionProvider of(){
        return new MultiFieldNameCompletionProvider();
    }

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> MULTI_FIELD_NAME_PATTERN =
            PlatformPatterns.psiElement().inside(
                    XmlPatterns.xmlAttributeValue(OrderBy.ATTR_FIELD_NAME,SearchFormInputs.ATTR_DEFAULT_ORDER_BY).inside(
                            XmlPatterns.xmlTag().withLocalName(OrderBy.TAG_NAME,SelectField.TAG_NAME,SearchFormInputs.TAG_NAME).inside(
                                    XmlPatterns.xmlTag().withLocalName(EntityFind.TAG_NAME, EntityFindCount.TAG_NAME,EntityFindOne.TAG_NAME)
                            )
                    )            );
    @Override
    public List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement) {
        List<String> inputedFields = getInputtedFieldSet(psiElement);

        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
        AbstractEntityName entityNameAbstract = null;

        entityNameAbstract= ServiceUtils.getCurrentEntityFind(psiElement).orElse(null);

        if(entityNameAbstract == null) {
            //再查找EntityFindCount
            entityNameAbstract = ServiceUtils.getCurrentEntityFindCount(psiElement).orElse(null);
        }

        if(entityNameAbstract==null)  return lookupElementBuilders ;

        ProgressManager.checkCanceled();

        final String name = MyDomUtils.getXmlAttributeValueString(entityNameAbstract.getEntityName())
                .orElse(MyStringUtils.EMPTY_STRING);
        if(MyStringUtils.isEmpty(name)) return lookupElementBuilders ;
        ProgressManager.checkCanceled();

        List<IndexAbstractField> fieldList = new ArrayList<>(EntityUtils.getEntityOrViewEntityFields(psiElement.getProject(), name));
        fieldList.forEach(item ->{
            String fieldName = MyDomUtils.getValueOrEmptyString(item.getName());
            if(!inputedFields.contains(fieldName)) {
                lookupElementBuilders.add(
                        LookupElementBuilder.create(fieldName)
                                .withCaseSensitivity(false)
                                .withTailText(MyStringUtils.formatFieldNameTrailText(MyDomUtils.getValueOrEmptyString(item.getType())))
                                .withTypeText(MyDomUtils.getValueOrEmptyString(item.getInAbstractIndexEntity().getShortName()))
                                .withInsertHandler(new FieldInsertHandler())
                );
            }
        });

        return lookupElementBuilders;
    }

    private List<String> getInputtedFieldSet(@NotNull PsiElement psiElement) {
        XmlAttribute xmlAttribute = MyDomUtils.getCurrentAttribute(psiElement).orElse(null);
        if (xmlAttribute == null ) return Collections.emptyList();
        if ( xmlAttribute.getValue() == null) return Collections.emptyList();

        final String inputStr = xmlAttribute.getValue().trim();
        if (inputStr.isEmpty()) return Collections.emptyList();

        String[] fieldSplits = MyStringUtils.getDummyFrontString(inputStr).split(",");
        return List.of(fieldSplits);
    }
    private static class FieldInsertHandler implements InsertHandler<LookupElement> {
        @Override
        public void handleInsert(@NotNull InsertionContext insertionContext, @NotNull LookupElement lookupElement) {
            if (insertionContext.getCompletionChar() == ',') {
                AutoPopupController.getInstance(insertionContext.getProject()).scheduleAutoPopup(insertionContext.getEditor());
            }

        }
    }
}
