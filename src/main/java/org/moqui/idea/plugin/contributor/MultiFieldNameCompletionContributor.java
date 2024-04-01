package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * OrderBy和SelectField属性进行Complete提示
 * 在OrderBy中，可以字段名前面可以添加字符：
 * + 表示顺序排列
 * - 表示倒序排列
 * ^ 表示大小写敏感
 */
public class MultiFieldNameCompletionContributor extends CompletionContributor {

  MultiFieldNameCompletionContributor(){
    extend(CompletionType.BASIC, getCapture(), new CompletionProvider<CompletionParameters>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement psiElement = parameters.getPosition();
        result.addAllElements(findCompletionItem(psiElement));
      }
    });
  }
  private static PsiElementPattern.Capture<PsiElement> getCapture() {
    return psiElement().inside(
            XmlPatterns.xmlAttributeValue(OrderBy.ATTR_FIELD_NAME,SearchFormInputs.ATTR_DEFAULT_ORDER_BY).inside(
                    XmlPatterns.xmlTag().withLocalName(OrderBy.TAG_NAME,SelectField.TAG_NAME,SearchFormInputs.TAG_NAME).inside(
                            XmlPatterns.xmlTag().withLocalName(EntityFind.TAG_NAME, EntityFindCount.TAG_NAME,EntityFindOne.TAG_NAME)
                    )
            )
    );
  }
  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    String prefix = result.getPrefixMatcher().getPrefix();


    int index = prefix.lastIndexOf(",");
    String str;
    if(index<0) {
      str = prefix;
    }else {
      str = prefix.substring(index+1);
    }
    String newPrefix = MyStringUtils.EMPTY_STRING;

    if(MyStringUtils.isNotEmpty(str)) {
      if (ServiceUtils.ORDER_BY_COMMANDER.contains(str.substring(0, 1))) {
        newPrefix = str.substring(1);
      }else {
        newPrefix = str;
      }
    }
    result = result.withPrefixMatcher(newPrefix);

    super.fillCompletionVariants(parameters,result);

//    if (parameters.getCompletionType() != CompletionType.BASIC) return;
//
//    super.fillCompletionVariants(parameters, result);
//    if(result.isStopped()){ return;}
//
//    PsiElement psiElement = parameters.getPosition();
//    //    //只处理属性值
//    if (MyDomUtils.isNotAttributeValue(psiElement)) return;
//
//    //获取属性名
//    final String xmlAttributeName = MyDomUtils.getCurrentAttributeName(psiElement).orElse(MyStringUtils.EMPTY_STRING);
//    if(!(xmlAttributeName.equals(OrderBy.ATTR_FIELD_NAME))) return;
//    //获取Tag名
//    final String xmlTagName = MyDomUtils.getCurrentTagName(psiElement).orElse(MyStringUtils.EMPTY_STRING);
//    if(!(xmlTagName.equals(OrderBy.TAG_NAME))) return;

//    result.stopHere();
    //重新定位需要匹配的内容
//    resetPosition(parameters,result);

    //获取可以匹配的内容
//    result.addAllElements(findCompletionItem(psiElement));

//    result.restartCompletionOnAnyPrefixChange();
  }



  private void resetPosition(@NotNull CompletionParameters parameters, CompletionResultSet result){
    PsiElement psiElement = parameters.getPosition();
    XmlAttribute xmlAttribute = MyDomUtils.getCurrentAttribute(psiElement).orElse(null);
    if (xmlAttribute == null) return;

    final String inputStr = xmlAttribute.getValue().trim();
    if (inputStr.isEmpty()) return;

    final String frontString = MyStringUtils.getDummyFrontString(inputStr);
    //处理最后一个字段
    int index = frontString.lastIndexOf(",");
    String lastFieldName;
    int textOffset = psiElement.getTextOffset();
    if(index<0) {
      lastFieldName = frontString;
    }else{
      lastFieldName= frontString.substring(index+1);
      textOffset = textOffset+index;

    }

    //判断第一个字符

    final String firstChar= lastFieldName.substring(0,1);
    if(ServiceUtils.ORDER_BY_COMMANDER.contains(firstChar)) {
      result = result.withPrefixMatcher(lastFieldName.substring(1));
      parameters = parameters.withPosition(psiElement,textOffset + 1);


    }else {
      result = result.withPrefixMatcher(lastFieldName);
      parameters = parameters.withPosition(psiElement,textOffset );


    }

  }
  private List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement){

    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    AbstractEntityName entityNameAbstract = null;

    entityNameAbstract= ServiceUtils.getCurrentEntityFind(psiElement).orElse(null);

    if(entityNameAbstract == null) {
      //再查找EntityFindCount
      entityNameAbstract = ServiceUtils.getCurrentEntityFindCount(psiElement).orElse(null);
    }

    if(entityNameAbstract==null)  return lookupElementBuilders;

    ProgressManager.checkCanceled();

    final String name = MyDomUtils.getXmlAttributeValueString(entityNameAbstract.getEntityName())
            .orElse(MyStringUtils.EMPTY_STRING);
    if(MyStringUtils.isEmpty(name)) return lookupElementBuilders;
    ProgressManager.checkCanceled();

    List<AbstractField> fieldList =new ArrayList<>();
    fieldList.addAll(EntityUtils.getEntityOrViewEntityFields(psiElement.getProject(),name));
    fieldList.stream().forEach(item ->{
      lookupElementBuilders.add(
              LookupElementBuilder.create(item.getName().getXmlAttributeValue().getValue())
                      .withCaseSensitivity(false)
      );
    });

    return lookupElementBuilders;
  }
}
