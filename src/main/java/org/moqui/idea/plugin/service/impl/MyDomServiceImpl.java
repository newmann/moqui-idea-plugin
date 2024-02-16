package org.moqui.idea.plugin.service.impl;

import org.moqui.idea.plugin.service.MyDomService;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.dom.model.Services;


import java.util.*;
import java.util.stream.Collectors;
@Deprecated
public class MyDomServiceImpl implements MyDomService {

  public static final String IDEA_INPUT_TAG = "IntellijIdeaRulezzz";
  @Override
  public @NotNull List<Services> getServicesList(@NotNull PsiClass psiClass, @Nullable GlobalSearchScope scope) {
    if (!psiClass.isInterface()) {
      return new ArrayList<>();
    }
    Project project = psiClass.getProject();
    List<DomFileElement<Services>> services = DomService.getInstance().getFileElements(Services.class, project, scope);
    //todo
    return services.stream()
        .map(DomFileElement::getRootElement)
        .filter(mapper ->
            psiClass.getQualifiedName().equals(
                mapper.getXmlTag().getValue()))
        .collect(Collectors.toList());
  }

  @Nullable
  @Override
  public Service getService(@NotNull PsiMethod psiMethod, @Nullable GlobalSearchScope scope) {
    PsiClass psiClass = psiMethod.getContainingClass();
    return null;

//    if (psiClass == null) {
//      return null;
//    }
//    List<Mapper> mappers = getMapper(psiClass, scope);
//    return mappers.stream().map(Mapper::getStatements).flatMap(Collection::stream)
//        .filter(statement -> StringUtils.equals(psiMethod.getName(), statement.getId().getValue()))
//        .findFirst().orElse(null);
  }

  @Override
  public @Nullable DomFileElement<Services> getServices(XmlFile xmlFile) {
    return DomManager.getDomManager(xmlFile.getProject()).getFileElement(xmlFile, Services.class);
  }

  @Override
  public @NotNull List<LookupElementBuilder> getLookupElement(PsiElement psiElement) {
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();

//    DomFileElement<Mapper> domFileElement = DomManager.getDomManager(psiElement.getProject())
//        .getFileElement((XmlFile) psiElement.getContainingFile(), Mapper.class);
//    Mapper mapper = domFileElement.getRootElement();
//    XmlTag xmlTag = PsiTreeUtil.getParentOfType(psiElement, XmlTag.class);
//    if (xmlTag == null) {
//      return lookupElementBuilders;
//    }
//    PsiClass psiClass = MyJavaUtil.findClass(mapper, xmlTag.getProject());
//    if (psiClass == null) {
//      return lookupElementBuilders;
//    }
//    DomElement domElement = DomManager.getDomManager(xmlTag.getProject()).getDomElement(xmlTag);
//    if (domElement instanceof Statement) {
//      List<LookupElementBuilder> builders = findLookupElement(psiClass, (Statement) domElement);
//      lookupElementBuilders.addAll(builders);
//    } else if (domElement instanceof Foreach) {
//      List<LookupElementBuilder> builders = findLookupElement((Foreach) domElement);
//      lookupElementBuilders.addAll(builders);
//    }
//    return lookupElementBuilders;
    Set<String> entityNames = new HashSet<>();
    entityNames.add("mantle.party.agreement.Agreement");
    entityNames.add("mantle.party.agreement.AgreementOrganizationPartyDetail");
    entityNames.add("mantle.party.agreement.AgreementServiceTime");
    entityNames.add("mantle.party.agreement.AgreementServiceCategory");


//    GlobalSearchScope scope = GlobalSearchScope.allScope(psiElement.getProject());
//
//    List<DomFileElement<Entities>> fileElementList  = DomService.getInstance().getFileElements(Entities.class,
//            psiElement.getProject(),scope);
//
//    for(DomFileElement<Entities> fileElement : fileElementList) {
//      //添加实体
//      for(Entity entity: fileElement.getRootElement().getEntities()) {
//        entityNames.add(entity.getPackage().getValue()+"."+entity.getEntityName().getValue());
//      };
//      //添加视图
//      for(ViewEntity viewEntity: fileElement.getRootElement().getViewEntities()) {
//        entityNames.add(viewEntity.getPackage().getValue()+"."+viewEntity.getEntityName().getValue());
//      };
//
//    }

    if(psiElement.getText().isBlank()) {
      lookupElementBuilders = entityNames.stream()
              .map(item->LookupElementBuilder.create((item))).collect(Collectors.toList());

    }else {
      String s = psiElement.getText();
//      s = s.substring(0, s.indexOf(IDEA_INPUT_TAG));
      final String inputStr = s.split(IDEA_INPUT_TAG)[0];//取前面的字符


      lookupElementBuilders = entityNames.stream().filter(item->item.indexOf(inputStr)>=0)
              .map(item->LookupElementBuilder.create(inputStr,item)).collect(Collectors.toList());

    }

    return lookupElementBuilders;

  }

  List<LookupElementBuilder> findLookupEntity(String partName) {
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();
    LookupElementBuilder builder = LookupElementBuilder.create("mantle.party.PartyRole");
    lookupElementBuilders.add(builder);
    builder = LookupElementBuilder.create("mantle.party.Party");
    lookupElementBuilders.add(builder);
    builder = LookupElementBuilder.create("mantle.party.Group");
    lookupElementBuilders.add(builder);

    return lookupElementBuilders;
  }

  List<LookupElementBuilder> findLookupElement(PsiClass psiClass, Service service) {
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();
//    PsiMethod[] methodsByName = psiClass.findMethodsByName(statement.getId().getValue(), true);
//    for (PsiMethod psiMethod : methodsByName) {
//      PsiParameterList parameterList = psiMethod.getParameterList();
//      if (parameterList.isEmpty()) {
//        continue;
//      }
//      for (int i = 0; i < parameterList.getParametersCount(); i++) {
//        PsiParameter parameter = parameterList.getParameter(i);
//        if (parameter != null) {
//          PsiClass parameterType = PsiTypesUtil.getPsiClass(parameter.getType());
//          if (PsiElementUtil.isMap(parameterType)) {
//            continue;
//          }
//          String name = getName(parameter);
//          LookupElementBuilder builder =
//              LookupElementBuilder.create(parameter, name);
//          lookupElementBuilders.add(builder);
//        }
//      }
//    }
    return lookupElementBuilders;
  }

//  List<LookupElementBuilder> findLookupElement(Foreach foreach) {
//    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();
//    String collectionName = foreach.getCollection().getStringValue();
//    if (StringUtils.isBlank(collectionName)) {
//      return lookupElementBuilders;
//    }
//    if (StringUtils.isBlank(foreach.getItem().getStringValue())) {
//      return lookupElementBuilders;
//    }
//    LookupElementBuilder builder =
//        LookupElementBuilder.create(foreach.getItem(), foreach.getItem().getStringValue());
//    lookupElementBuilders.add(builder);
//    return lookupElementBuilders;
//  }

  public String getName(PsiParameter parameter) {
//    PsiAnnotation annotation = parameter.getAnnotation("org.apache.ibatis.annotations.Param");
//    if (annotation == null) {
//      return parameter.getName();
//    }
//    PsiAnnotationParameterList annotationParameterList = annotation.getParameterList();
//    PsiNameValuePair[] attributes = annotationParameterList.getAttributes();
//    for (PsiNameValuePair attribute : attributes) {
//      if (StringUtils.isNotBlank(attribute.getLiteralValue())) {
//        return attribute.getLiteralValue();
//      }
//    }
    return null;
  }
}
