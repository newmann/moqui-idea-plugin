package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.ProcessingContext;
import org.assertj.core.util.Sets;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.patterns.PlatformPatterns.psiFile;

/**
 * OrderBy和SelectField属性进行Complete提示
 * 在OrderBy中，可以字段名前面可以添加字符：
 * + 表示顺序排列
 * - 表示倒序排列
 * ^ 表示大小写敏感
 */
public class MultiFieldNameCompletionContributor extends CompletionContributor {

  MultiFieldNameCompletionContributor(){
//    extend(CompletionType.BASIC, getCommaCapture(), new CompletionProvider<>() {
//      @Override
//      protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
////        String typedText = parameters.getEditor().getDocument().getCharsSequence().subSequence(parameters.getOffset(), parameters.getOffset()).toString();
////        if(typedText.equals(",")) {
////          result.restartCompletionOnAnyPrefixChange();
////        }else {
//
//        findCompletionItem(parameters,context,result);
////        }
//      }
//    });

    extend(CompletionType.BASIC, getCapture(), new CompletionProvider<>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
//        String typedText = parameters.getEditor().getDocument().getCharsSequence().subSequence(parameters.getOffset()-2, parameters.getOffset()-1).toString();
//        if(typedText.equals(",")) {
//          result.restartCompletionOnAnyPrefixChange();
//        }
        findCompletionItem(parameters,context,result);

      }
    });

  }



//  @Override
//  public void beforeCompletion(@NotNull CompletionInitializationContext context) {
//    PsiFile file = context.getFile();
//    int offset = context.getStartOffset();
//    PsiElement element = file.findElementAt(offset);
//    if(element == null) return;
//    if(element.getText().equals(",")) {
//      context.getEditor().getCaretModel().moveToOffset(offset);
////      context.getEditor().getCaretModel().runForEachCaret(caret ->{
////        context.
////      });
//    }
//
//  }

  public static PsiElementPattern.Capture<PsiElement> getCapture() {
    return psiElement().inside(
            XmlPatterns.xmlAttributeValue(OrderBy.ATTR_FIELD_NAME,SearchFormInputs.ATTR_DEFAULT_ORDER_BY).inside(
                    XmlPatterns.xmlTag().withLocalName(OrderBy.TAG_NAME,SelectField.TAG_NAME,SearchFormInputs.TAG_NAME).inside(
                            XmlPatterns.xmlTag().withLocalName(EntityFind.TAG_NAME, EntityFindCount.TAG_NAME,EntityFindOne.TAG_NAME)
                    )
            )
    );
  }
  private static PsiElementPattern.Capture<PsiElement> getCommaCapture() {
    return psiElement().afterLeaf(",");

    //    return psiElement().afterLeaf(",").withParent(
//            XmlPatterns.xmlAttributeValue(OrderBy.ATTR_FIELD_NAME,SearchFormInputs.ATTR_DEFAULT_ORDER_BY).inside(
//                    XmlPatterns.xmlTag().withLocalName(OrderBy.TAG_NAME,SelectField.TAG_NAME,SearchFormInputs.TAG_NAME).inside(
//                            XmlPatterns.xmlTag().withLocalName(EntityFind.TAG_NAME, EntityFindCount.TAG_NAME,EntityFindOne.TAG_NAME)
//                    )
//            )
//    );
  }

//    @Override
//  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
//    String prefix = result.getPrefixMatcher().getPrefix();
//
//
//    int index = prefix.lastIndexOf(",");
//    String str;
//    if(index<0) {
//      str = prefix;
//    }else {
//      str = prefix.substring(index+1);
//    }
//    String newPrefix = MyStringUtils.EMPTY_STRING;
//
//    if(MyStringUtils.isNotEmpty(str)) {
//      if (ServiceUtils.ORDER_BY_COMMANDER.contains(str.substring(0, 1))) {
//        newPrefix = str.substring(1);
//      }else {
//        newPrefix = str;
//      }
//    }
//    result = result.withPrefixMatcher(newPrefix);

//  }


    private List<String> getInputedFieldSet(@NotNull CompletionParameters parameters) {
    PsiElement psiElement = parameters.getPosition();
    XmlAttribute xmlAttribute = MyDomUtils.getCurrentAttribute(psiElement).orElse(null);
    if (xmlAttribute == null ) return Collections.emptyList();
    if ( xmlAttribute.getValue() == null) return Collections.emptyList();

    final String inputStr = xmlAttribute.getValue().trim();
    if (inputStr.isEmpty()) return Collections.emptyList();

    String[] fieldSplits = MyStringUtils.getDummyFrontString(inputStr).split(",");
    return List.of(fieldSplits);
  }
//  private void resetPosition(@NotNull CompletionParameters parameters, CompletionResultSet result){
//    PsiElement psiElement = parameters.getPosition();
//    XmlAttribute xmlAttribute = MyDomUtils.getCurrentAttribute(psiElement).orElse(null);
//    if (xmlAttribute == null ) return;
//    if ( xmlAttribute.getValue() == null) return;
//
//    final String inputStr = xmlAttribute.getValue().trim();
//    if (inputStr.isEmpty()) return;
//
//    final String frontString = MyStringUtils.getDummyFrontString(inputStr);
//    //处理最后一个字段
//    int index = frontString.lastIndexOf(",");
//    String lastFieldName;
//    int textOffset = psiElement.getTextOffset();
//    if(index<0) {
//      lastFieldName = frontString;
//    }else{
//      lastFieldName= frontString.substring(index+1);
//      textOffset = textOffset+index;
//
//    }
//
//    //判断第一个字符
//
//    final String firstChar= lastFieldName.substring(0,1);
//    if(ServiceUtils.ORDER_BY_COMMANDER.contains(firstChar)) {
//      result = result.withPrefixMatcher(lastFieldName.substring(1));
//      parameters = parameters.withPosition(psiElement,textOffset + 1);
//
//
//    }else {
//      result = result.withPrefixMatcher(lastFieldName);
//      parameters = parameters.withPosition(psiElement,textOffset );
//
//
//    }
//
//  }
  private void findCompletionItem(@NotNull CompletionParameters parameters,ProcessingContext context, @NotNull CompletionResultSet result){
    PsiElement psiElement = parameters.getPosition();

    List<String> inputedFields = getInputedFieldSet(parameters);

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
