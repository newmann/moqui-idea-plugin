package org.moqui.idea.plugin.service.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.util.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.intellij.psi.xml.XmlTokenType.XML_NAME;

public class ServiceCallTagFindRelatedItemService implements FindRelatedItemService {
  private boolean crudServiceCall = false;

  public static ServiceCallTagFindRelatedItemService INSTANCE = new ServiceCallTagFindRelatedItemService();

  private ServiceCallTagFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {


    if(!(psiElement instanceof XmlToken)) return false;
    XmlToken token = (XmlToken) psiElement;
    if(!token.getTokenType().equals(XML_NAME)) return false;
    if(!(token.getText().equals(ServiceCall.TAG_NAME))) return false;

    if(!(psiElement.getParent() instanceof XmlTag))  return false;

    if(!(ServiceUtils.isServicesFile(psiElement.getContainingFile())
            || SecaUtils.isSecasFile(psiElement.getContainingFile())
            || EecaUtils.isEecasFile(psiElement.getContainingFile())
            || ScreenUtils.isScreenFile(psiElement.getContainingFile())
    )) return false;

    return true;

  }

  @Override
  public List<PsiElement> findRelatedItem(PsiElement psiElement) {

    XmlToken xmlToken = (XmlToken) psiElement;
    XmlTag xmlTag =(XmlTag) xmlToken.getParent();
    if (xmlTag == null) return new ArrayList<>();

    final String fullName = xmlTag.getAttributeValue(ServiceCall.ATTR_NAME);
    if (fullName == null) return new ArrayList<>();

    Optional<String> optEntityName = EntityUtils.getEntityNameFromServiceCallName(fullName);
    if (optEntityName.isEmpty()) {
      //按查找service的方式查找
      crudServiceCall = false;
      return ServiceUtils.getRelatedService(psiElement,fullName);
    } else {
      //按查找entity的方式来查找
      crudServiceCall = true;
      return EntityUtils.getRelatedEntity(psiElement,optEntityName.get());

    }

  }

//  /**
//   *  从对Entity的标准操作中获取EntityName
//   *  格式为create/update/delete#mantle.order.OrderItem
//   * @param fullName
//   * @return
//   */
//  private Optional<String> getEntityName(@NotNull String fullName) {
//    String[] names = fullName.split("#");
//
//    if(names.length != 2) return Optional.empty();
//
//    if((names[0].indexOf(".") < 0) && (names[1].indexOf(".") > 0)) {
//      return  Optional.of(names[1]);
//    }else {
//      return Optional.empty();
//    }
//
//  }
  @Override
  public Icon getNagavitorToIcon() {
    if(crudServiceCall) {
      return EntityUtils.getNagavitorToEntityIcon();
    }else {

      return ServiceUtils.getNagavitorToServiceIcon();
    }
  }

  @Override
  public String getToolTips() {
    if(crudServiceCall) {
      return EntityUtils.getNagavitorToEntityToolTips();
    }else {
      return ServiceUtils.getNagavitorToServiceToolTips();
    }
  }

}
