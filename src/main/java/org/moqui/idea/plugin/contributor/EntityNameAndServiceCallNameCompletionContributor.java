package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.CompletionType;
import icons.MoquiIcons;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.icon.MyIcons;
import org.moqui.idea.plugin.util.MyDomUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ServiceUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import java.util.*;
import java.util.Set;
import java.util.stream.Collectors;

import static com.intellij.psi.xml.XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;

/**
 * 快速显示实体和服务的名称，
 * 显示实体的全名，即：包名+表名
 * 显示服务全名，即：包名.动作#名称
 */
@Deprecated
public class EntityNameAndServiceCallNameCompletionContributor extends CompletionContributor {
//  private static final String IDEA_INPUT_TAG = "IntellijIdeaRulezzz";
  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    if (parameters.getCompletionType() != CompletionType.BASIC) return;

    super.fillCompletionVariants(parameters, result);
    if(result.isStopped()){ return;}
    PsiElement psiElement = parameters.getPosition();
//
//    //只处理属性值
    if (!(psiElement instanceof XmlToken) ) return;
    if(!((XmlToken) psiElement).getTokenType().equals(XML_ATTRIBUTE_VALUE_TOKEN)) return;

//    if (!(parameters.getPosition().getParent() instanceof XmlAttributeValue)) {
//      return;
//    }
    //获取属性名
    XmlAttribute xmlAttribute = PsiTreeUtil.getParentOfType(parameters.getPosition(), XmlAttribute.class);
    if (xmlAttribute == null ) {
      return;
    }
    //获取输入的属性内容
    String inputStr = MyStringUtils.removeDummy(xmlAttribute.getValue());

    final String attributeName = xmlAttribute.getName();
    //获取Tag名
    XmlTag xmlTag = PsiTreeUtil.getParentOfType(parameters.getPosition(), XmlTag.class);
    if (xmlTag == null ) {
      return;
    }
    final String tagName = xmlTag.getName();


    //获取RootTag名
    PsiFile psiFile = parameters.getOriginalFile();
    Optional<String> optRootTagName = MyDomUtils.getRootTagName(psiFile);
    if (optRootTagName.isEmpty()) return;
    final String rootTagName = optRootTagName.get();

    //根据 rootTagName，tagName, attributeName 进行分别处理
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    switch (rootTagName) {
      case Services.TAG_NAME:
        if(attributeName.equals("entity-name")) {
          lookupElementBuilders = lookupEntityAndViewEntity(psiElement.getProject(),inputStr);
        }
        if(attributeName.equals("name") && tagName.equals("service-call")) {
          lookupElementBuilders = lookupService(psiElement.getProject(),inputStr);
        }
        break;

      case Entities.TAG_NAME:


//        if(attributeName.equals(Relationship.ATTR_RELATED) && tagName.equals(Relationship.TAG_NAME)) {
//          //如果是Entity下的relatihoship，则只能显示Entity
//          XmlTag parentTag = xmlTag.getParentTag();
//          if(parentTag.getName().equals(Entity.TAG_NAME)) {
//            lookupElementBuilders = lookupEntity(psiElement.getProject(),inputStr);
//          }else {
//            lookupElementBuilders = lookupEntityAndViewEntity(psiElement.getProject(),inputStr);
//          }
//
//        }
//
//        if(attributeName.equals(MemberEntity.ATTR_ENTITY_NAME) && tagName.equals(MemberEntity.TAG_NAME)) {
//          lookupElementBuilders = lookupEntityAndViewEntity(psiElement.getProject(),inputStr);
//        }

        break;
      case Secas.TAG_NAME:
        if(attributeName.equals("name") && tagName.equals("service-call")) {
          lookupElementBuilders = lookupService(psiElement.getProject(),inputStr);
        }

          break;
      case Eecas.TAG_NAME:
        if(attributeName.equals("entity")) {
          lookupElementBuilders = lookupEntityAndViewEntity(psiElement.getProject(),inputStr);
        }
        if(attributeName.equals("name") && tagName.equals("service-call")) {
          lookupElementBuilders = lookupService(psiElement.getProject(),inputStr);
        }
        break;
      default:
        return;
    }


    result.addAllElements(lookupElementBuilders);
  }

  private List<LookupElementBuilder> lookupEntityAndViewEntity(@NotNull Project project, @NotNull String inputStr){

    int charIndex = inputStr.lastIndexOf('.');

//    Map<String, DomElement> entityMap = EntityUtils.findAllEntityDomElement(project);
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    java.util.Set<String> entitySet;
    java.util.Set<String> viewSet;
    if (charIndex < 0) {
      entitySet = EntityUtils.getEntityFullNames(project,"");
      viewSet = EntityUtils.getViewEntityFullNames(project,"");

    }else {
      final String packageName = inputStr.substring(0,charIndex);
      entitySet = EntityUtils.getEntityFullNames(project,packageName);
      viewSet = EntityUtils.getViewEntityFullNames(project,packageName);
    }

    entitySet.forEach((item) -> {
      lookupElementBuilders.add(createEntityLookupElement(item));
    });
    viewSet.forEach((item) -> {
      lookupElementBuilders.add(createViewEntityLookupElement(item));
    });

    return lookupElementBuilders;
  }
  private List<LookupElementBuilder> lookupEntity(@NotNull Project project, @NotNull String inputStr){

    int charIndex = inputStr.lastIndexOf('.');

//    Map<String, DomElement> entityMap = EntityUtils.findAllEntityDomElement(project);
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    java.util.Set<String> entitySet;
    if (charIndex < 0) {
      entitySet = EntityUtils.getEntityFullNames(project,"");


    }else {
      final String packageName = inputStr.substring(0,charIndex);
      entitySet = EntityUtils.getEntityFullNames(project,packageName);

    }
    entitySet.forEach((item) -> {
      lookupElementBuilders.add(createEntityLookupElement(item));
    });

    return lookupElementBuilders;
  }

  private List<LookupElementBuilder> lookupService(@NotNull Project project,@NotNull String inputStr){
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    int slashIndex = inputStr.indexOf('#');
    String[] slashSplit = inputStr.split("#");
    java.util.Set<String> entitySet =new HashSet<>();
    java.util.Set<String> serviceSet = new HashSet<>();
    java.util.Set<String> crudSet = new HashSet<>();
    if(slashIndex >= 0) {
      //#存在，需要进行进一步判断
      int pointIndex = slashSplit[0].lastIndexOf('.');
      if(pointIndex<0) {
        //标准操作,CRUD
        //操作不存在，则返回空
        if(! ServiceUtils.STANDARD_CRUD_COMMANDER.contains(slashSplit[0])) return lookupElementBuilders;
        //操作存在，则返回entityNameList，需要根据后半部分进行过滤
        if(slashSplit.length==1) {
          entitySet.addAll(EntityUtils.getEntityFullNames(project,""));
        }else {
          int backPointIndex = slashSplit[1].lastIndexOf('.');
          if(backPointIndex<0) {
            entitySet.addAll(EntityUtils.getEntityFullNames(project,""));
          }else {
            var packageName = slashSplit[1].substring(0, backPointIndex);
            entitySet.addAll(EntityUtils.getEntityFullNames(project,packageName));
          }
        }
        //将CRUD放到entityName前面，以便识别
        entitySet = entitySet.stream().map(name->slashSplit[0] + "#" + name).collect(Collectors.toSet());

      }else{
        //Service Call
        var className= slashSplit[0].substring(0, pointIndex);

        getServiceCallToSet(project,className,serviceSet);

//        serviceSet.addAll(ServiceUtils.findServiceClassNameSet(project,className));
//        if (serviceSet.size()==1) {
//          //当前className是个完整的名称，则取该class下的所有服务
//          serviceSet.clear();
//          serviceSet.addAll(ServiceUtils.getServiceFullNameByClassName(project,className));
//        }

      }
    }else {
      //#不存在，也需要进一步判断是否有“.”，没有这这个字符则需要将CRUD操作放入返回列表中
      int pointIndex = inputStr.lastIndexOf('.');
      if(pointIndex<0) {
        //没有，则取所有的className和CRUD
        crudSet.addAll(ServiceUtils.STANDARD_CRUD_COMMANDER);
        getServiceCallToSet(project,"",serviceSet);
      }else {
        //取类的过滤字符串
        var className= inputStr.substring(0, pointIndex);
        getServiceCallToSet(project,className,serviceSet);
//        serviceSet.addAll(ServiceUtils.findServiceClassNameSet(project,className));

      }
    }
    crudSet.forEach(item->{
      LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(item)
              .withCaseSensitivity(true).withIcon(MoquiIcons.CrudEntity);
      lookupElementBuilders.add(lookupElementBuilder);

    });

    entitySet.forEach(item->{
      LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(item)
              .withCaseSensitivity(true).withIcon(MoquiIcons.CrudEntity);
      lookupElementBuilders.add(lookupElementBuilder);

    });
    serviceSet.forEach(item->{
      LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(item)
              .withCaseSensitivity(true).withIcon(MoquiIcons.NavigateToService);
      lookupElementBuilders.add(lookupElementBuilder);

    });


//    int pointIndex = inputStr.lastIndexOf('.');
//    int slashIndex = inputStr.lastIndexOf("#");
//
//    Map<String, DomElement> entityMap = ServiceUtils.findAllServiceDomElement(project);


//    entityMap.forEach((key,value) ->{
//      LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(value,key)
//              .withCaseSensitivity(true).withIcon(AllIcons.Ide.ConfigFile);
//      lookupElementBuilders.add(lookupElementBuilder);
//    });

    return lookupElementBuilders;
  }
  private void getServiceCallToSet(@NotNull Project project, @NotNull String filterClassName, @NotNull Set<String> resultSet){

    resultSet.addAll(ServiceUtils.findServiceClassNameSet(project,filterClassName));
    if (resultSet.size()==1) {
      //当前className是个完整的名称，则取该class下的所有服务
      resultSet.clear();
      resultSet.addAll(ServiceUtils.getServiceFullNameInClass(project,filterClassName));
    }
  }

  private LookupElementBuilder createLookupElement(@NotNull String key, @NotNull DomElement element){
    Icon icon;

    icon = EntityUtils.getIconByTagName(element.getXmlTag().getName());
    LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(element, key)
            .withCaseSensitivity(false)
//                  .withPresentableText(key.substring(charIndex+1))
            .withTailText("(" + element.getXmlTag().getName() + ")");
    if(!(icon == null)) {
      lookupElementBuilder.withIcon(icon);
    }
    return lookupElementBuilder;
  }
  private LookupElementBuilder createEntityLookupElement(@NotNull String entityName ){
    Icon icon = EntityUtils.getNagavitorToEntityIcon();

    LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(entityName)
            .withCaseSensitivity(false).withIcon(icon);
//                  .withPresentableText(key.substring(charIndex+1))
//            .withTailText("(" + element.getXmlTag().getName() + ")");
    return lookupElementBuilder;
  }
  private LookupElementBuilder createViewEntityLookupElement(@NotNull String viewName ){
    Icon icon = EntityUtils.getNagavitorToViewIcon();

    LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(viewName)
            .withCaseSensitivity(false).withIcon(icon);
//                  .withPresentableText(key.substring(charIndex+1))
//            .withTailText("(" + element.getXmlTag().getName() + ")");
    return lookupElementBuilder;
  }

  //  private List<LookupElementBuilder> lookupService(@NotNull Project project,@NotNull String inputStr){
////    ServicePsiElementService servicePsiElementService =
////            project.getService(ServicePsiElementService.class);
////
////
////    Map<String, DomElement> entityMap = servicePsiElementService.getAllServiceDomElements();
//    Map<String, DomElement> entityMap;
//    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
//
//    if(inputStr.contains(".")) {
//
//      java.util.Set<String> result = ServiceUtils.findServiceDomElementByInputStr(project,inputStr);
//      result.forEach(item->{
//        lookupElementBuilders.add(LookupElementBuilder.create(item,"").withIcon(AllIcons.Ide.UpDown)
//                .withCaseSensitivity(true));
//      });
//
//    }else {
//      entityMap = ServiceUtils.findAllServiceDomElement(project);
//      entityMap.forEach((key,value) ->{
//        LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(value,key)
//                .withCaseSensitivity(true).withIcon(AllIcons.Ide.ConfigFile);
//        lookupElementBuilders.add(lookupElementBuilder);
//      });
//    }
//    return lookupElementBuilders;
//  }
}
