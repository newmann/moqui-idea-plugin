package org.moqui.idea.plugin.service.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlToken;
import org.moqui.idea.plugin.dom.model.Seca;
import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.SecaUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import javax.swing.*;
import java.util.List;
import java.util.Optional;

import static com.intellij.psi.xml.XmlTokenType.XML_NAME;

public class SecaTagFindRelatedItemService implements FindRelatedItemService {
  private boolean crudServiceCall = false;

  public static SecaTagFindRelatedItemService INSTANCE = new SecaTagFindRelatedItemService();

  private SecaTagFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {


    if(!(psiElement instanceof XmlToken)) return false;
    XmlToken token = (XmlToken) psiElement;
    if(!token.getTokenType().equals(XML_NAME)) return false;
    if(!(token.getText().equals(Seca.ATTR_SERVICE))) return false;

    if(!(psiElement.getParent() instanceof XmlAttribute))  return false;

    if(!(SecaUtils.isSecasFile(psiElement.getContainingFile())
    )) return false;

    return true;

  }

  @Override
  public List<PsiElement> findRelatedItem(PsiElement psiElement) {

    XmlToken xmlToken = (XmlToken) psiElement;
    XmlAttribute xmlAttribute =(XmlAttribute) xmlToken.getParent();

    final String fullName = xmlAttribute.getValue();
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
