package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.ProcessingContext;
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

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * OrderBy和SelectField属性进行Complete提示
 * 在OrderBy中，可以字段名前面可以添加字符：
 * + 表示顺序排列
 * - 表示倒序排列
 * ^ 表示大小写敏感
 */
@Deprecated
public class MultiFieldNameCompletionContributor extends CompletionContributor {

  MultiFieldNameCompletionContributor(){
    extend(CompletionType.BASIC, getCapture(), new CompletionProvider<>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {

        findCompletionItem(parameters,context,result);

      }
    });

  }





  public static PsiElementPattern.Capture<PsiElement> getCapture() {
    return psiElement().inside(
            XmlPatterns.xmlAttributeValue(OrderBy.ATTR_FIELD_NAME,SearchFormInputs.ATTR_DEFAULT_ORDER_BY).inside(
                    XmlPatterns.xmlTag().withLocalName(OrderBy.TAG_NAME,SelectField.TAG_NAME,SearchFormInputs.TAG_NAME).inside(
                            XmlPatterns.xmlTag().withLocalName(EntityFind.TAG_NAME, EntityFindCount.TAG_NAME,EntityFindOne.TAG_NAME)
                    )
            )
    );
  }



    private List<String> getInputedFieldSet(@NotNull PsiElement psiElement) {
    XmlAttribute xmlAttribute = MyDomUtils.getCurrentAttribute(psiElement).orElse(null);
    if (xmlAttribute == null ) return Collections.emptyList();
    if ( xmlAttribute.getValue() == null) return Collections.emptyList();

    final String inputStr = xmlAttribute.getValue().trim();
    if (inputStr.isEmpty()) return Collections.emptyList();

    String[] fieldSplits = MyStringUtils.getDummyFrontString(inputStr).split(",");
    return List.of(fieldSplits);
  }

  private void findCompletionItem(@NotNull CompletionParameters parameters,ProcessingContext context, @NotNull CompletionResultSet result){
    PsiElement psiElement = parameters.getPosition();

    List<String> inputedFields = getInputedFieldSet(psiElement);

    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    AbstractEntityName entityNameAbstract = null;

    entityNameAbstract= ServiceUtils.getCurrentEntityFind(psiElement).orElse(null);

    if(entityNameAbstract == null) {
      //再查找EntityFindCount
      entityNameAbstract = ServiceUtils.getCurrentEntityFindCount(psiElement).orElse(null);
    }

    if(entityNameAbstract==null)  return ;

    ProgressManager.checkCanceled();

    final String name = MyDomUtils.getXmlAttributeValueString(entityNameAbstract.getEntityName())
            .orElse(MyStringUtils.EMPTY_STRING);
    if(MyStringUtils.isEmpty(name)) return ;
    ProgressManager.checkCanceled();

      List<IndexAbstractField> fieldList = new ArrayList<>(EntityUtils.getEntityOrViewEntityFields(psiElement.getProject(), name));
    fieldList.forEach(item ->{
      String fieldName = MyDomUtils.getValueOrEmptyString(item.getName());
      if(!inputedFields.contains(fieldName)) {
        lookupElementBuilders.add(
                LookupElementBuilder.create(fieldName)
                        .withCaseSensitivity(false)
                        .withTypeText(MyDomUtils.getValueOrEmptyString(item.getType()))
                        .withInsertHandler(new FieldInsertHandler())
        );
      }
    });

    result.addAllElements(lookupElementBuilders);
    result.addLookupAdvertisement("查找字段");

  }

  private static class FieldInsertHandler implements InsertHandler{
    @Override
    public void handleInsert(@NotNull InsertionContext insertionContext, @NotNull LookupElement lookupElement) {
      if (insertionContext.getCompletionChar() == ',') {
        AutoPopupController.getInstance(insertionContext.getProject()).scheduleAutoPopup(insertionContext.getEditor());
      }

    }
  }
}
