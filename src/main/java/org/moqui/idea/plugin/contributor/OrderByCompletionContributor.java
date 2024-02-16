package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlToken;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.dom.model.EntityFind;
import org.moqui.idea.plugin.dom.model.Field;
import org.moqui.idea.plugin.dom.model.OrderBy;
import org.moqui.idea.plugin.service.EditCompletionService;
import org.moqui.idea.plugin.service.FindEditCompletionServiceFactory;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.xml.XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;

/**
 * OrderBy属性进行Complete提示
 * 在字段名前面可以添加字符：
 * + 表示顺序排列
 * - 表示倒序排列
 * ^ 表示大小写敏感
 */
public class OrderByCompletionContributor extends CompletionContributor {

  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    if (parameters.getCompletionType() != CompletionType.BASIC) return;

    super.fillCompletionVariants(parameters, result);
    if(result.isStopped()){ return;}

    PsiElement psiElement = parameters.getPosition();
    //    //只处理属性值
    if (MyDomUtils.isNotAttributeValue(psiElement)) return;

    //获取属性名
    final String xmlAttributeName = MyDomUtils.getCurrentAttributeName(psiElement).orElse(MyStringUtils.EMPTY_STRING);
    if(!(xmlAttributeName.equals(OrderBy.ATTR_FIELD_NAME))) return;
    //获取Tag名
    final String xmlTagName = MyDomUtils.getCurrentTagName(psiElement).orElse(MyStringUtils.EMPTY_STRING);
    if(!(xmlTagName.equals(OrderBy.TAG_NAME))) return;

    //重新定位需要匹配的内容
    resetPosition(parameters,result);
    //获取可以匹配的内容

    result.addAllElements(findCompletionItem(psiElement));

    result.restartCompletionWhenNothingMatches();
  }



  private void resetPosition(@NotNull CompletionParameters parameters, CompletionResultSet result){
    PsiElement psiElement = parameters.getPosition();
    XmlAttribute xmlAttribute = MyDomUtils.getCurrentAttribute(psiElement).orElse(null);
    if (xmlAttribute == null) return;
    //判断第一个字符
    final String inputStr = xmlAttribute.getValue().trim();
    if (inputStr.length() == 0) return;

    final String firstChar= inputStr.substring(0,1);
    if(ServiceUtils.ORDER_BY_COMMANDER.contains(firstChar)) {
      final String prefix = MyStringUtils.getDummyFrontString(inputStr);

      parameters.withPosition(psiElement,psiElement.getTextOffset() + 1);
      result.withPrefixMatcher(prefix.substring(1));

    }

  }
  private List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement){

    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    EntityFind entityFind= ServiceUtils.getCurrentEntityFind(psiElement).orElse(null);
    if(entityFind == null) return lookupElementBuilders;
    ProgressManager.checkCanceled();

    final String name = MyDomUtils.getXmlAttributeValueString(entityFind.getEntityName().getXmlAttributeValue())
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
