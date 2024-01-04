package org.moqui.idea.plugin.contributor;

import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.EntityPsiElementService;
import org.moqui.idea.plugin.service.ServicePsiElementService;
import org.moqui.idea.plugin.util.DomUtils;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.intellij.psi.xml.XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN;

/**
 * 快速显示实体名称，
 * 用在service等需要用到实体的地方，都是指向实体的全名，即：包名+表名
 */
public class EntityNameCompletionContributor extends CompletionContributor {
//  private static final String IDEA_INPUT_TAG = "IntellijIdeaRulezzz";
  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
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
    final String attributeName = xmlAttribute.getName();
    //获取Tag名
    XmlTag xmlTag = PsiTreeUtil.getParentOfType(parameters.getPosition(), XmlTag.class);
    if (xmlTag == null ) {
      return;
    }
    final String tagName = xmlTag.getName();


    //获取RootTag名
    PsiFile psiFile = parameters.getOriginalFile();
    Optional<String> optRootTagName = DomUtils.getRootTagName(psiFile);
    if (optRootTagName.isEmpty()) return;
    final String rootTagName = optRootTagName.get();

    //根据 rootTagName，tagName, attributeName 进行分别处理
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    switch (rootTagName) {
      case Services.TAG_NAME:
        if(attributeName.equals("entity-name")) {
          lookupElementBuilders = lookupEntity(psiElement.getProject());
        }
        if(attributeName.equals("name") && tagName.equals("service-call")) {
          lookupElementBuilders = lookupService(psiElement.getProject());
        }
        break;

      case Entities.TAG_NAME:
        if(attributeName.equals(Relationship.ATTR_RELATED) && tagName.equals(Relationship.TAG_NAME)) {
          lookupElementBuilders = lookupEntity(psiElement.getProject());
        }

        if(attributeName.equals(MemberEntity.ATTR_ENTITY_NAME) && tagName.equals(MemberEntity.TAG_NAME)) {
          lookupElementBuilders = lookupEntity(psiElement.getProject());
        }

        break;
      case Secas.TAG_NAME:
        if(attributeName.equals("name") && tagName.equals("service-call")) {
          lookupElementBuilders = lookupService(psiElement.getProject());
        }

          break;
      case Eecas.TAG_NAME:
        if(attributeName.equals("entity")) {
          lookupElementBuilders = lookupEntity(psiElement.getProject());
        }
        if(attributeName.equals("name") && tagName.equals("service-call")) {
          lookupElementBuilders = lookupService(psiElement.getProject());
        }
        break;
      default:
        return;
    }



    //获取attribute name，根据名字进行不同的处理
//    try {
//      if(psiElement.getParent() == null) return;
//
//      PsiElement entityElement = psiElement.getParent().getParent();
//      if(entityElement == null) return;
//
//      if (!(entityElement instanceof XmlAttribute)) {
//        return;
//      }
//
//      String attributeName = ((XmlAttribute) entityElement).getName();
//      if (!attributeName.equals(Entity.TAG_NAME)) return;
//    }catch(Exception e){
//      CustomNotifier.error(psiElement.getProject(), e.getMessage());
//      return;
//    }



//    EntityPsiElementService entityPsiElementService =
//            psiElement.getProject().getService(EntityPsiElementService.class);
//
//
//    Map<String, DomElement> entityMap = entityPsiElementService.getAllEntityDomElements();
//    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
//    entityMap.forEach((key,value) ->{
//      LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(value,key)
//              .withCaseSensitivity(true).withIcon(AllIcons.Ide.Gift);
//      lookupElementBuilders.add(lookupElementBuilder);
//    });

    result.addAllElements(lookupElementBuilders);
  }

  private List<LookupElementBuilder> lookupEntity(@NotNull Project project){
    EntityPsiElementService entityPsiElementService =
            project.getService(EntityPsiElementService.class);


    Map<String, DomElement> entityMap = entityPsiElementService.getAllEntityDomElements();
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    entityMap.forEach((key,value) ->{
      LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(value,key)
              .withCaseSensitivity(true).withIcon(AllIcons.Ide.Gift);
      lookupElementBuilders.add(lookupElementBuilder);
    });

    return lookupElementBuilders;
  }
  private List<LookupElementBuilder> lookupService(@NotNull Project project){
    ServicePsiElementService servicePsiElementService =
            project.getService(ServicePsiElementService.class);


    Map<String, DomElement> entityMap = servicePsiElementService.getAllServiceDomElements();
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<LookupElementBuilder>();
    entityMap.forEach((key,value) ->{
      LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(value,key)
              .withCaseSensitivity(true).withIcon(AllIcons.Ide.ConfigFile);
      lookupElementBuilders.add(lookupElementBuilder);
    });

    return lookupElementBuilders;
  }

}
