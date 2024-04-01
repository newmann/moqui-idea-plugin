package org.moqui.idea.plugin.service.impl;

import org.moqui.idea.plugin.dom.model.AbstractEntity;
import org.moqui.idea.plugin.dom.model.AbstractEntityName;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ScreenUtils;
import org.moqui.idea.plugin.util.ServiceUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlToken;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.xml.XmlTokenType.XML_NAME;

public class EntityNameAttributeFindRelatedItemService implements FindRelatedItemService {
  public static EntityNameAttributeFindRelatedItemService INSTANCE = new EntityNameAttributeFindRelatedItemService();

  private AbstractEntity abstractEntity;
  private EntityNameAttributeFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {
    if(!(psiElement instanceof XmlToken xmlToken)) return false;
    if(!xmlToken.getTokenType().equals(XML_NAME)) return false;

    if(!(xmlToken.getText().equals(AbstractEntityName.ATTR_ENTITY_NAME))) return false;
    PsiElement tokenParent = xmlToken.getParent();
    if(tokenParent == null) return false;
    if(!(tokenParent instanceof XmlAttribute))  return false;

    return ServiceUtils.isServicesFile(psiElement.getContainingFile())
              || ScreenUtils.isScreenFile(psiElement.getContainingFile());
  }

  @Override
  public List<PsiElement> findRelatedItem(PsiElement psiElement) {
    List<PsiElement> resultList = new ArrayList<>();
    XmlToken xmlToken = (XmlToken) psiElement;
    XmlAttribute xmlAttribute =(XmlAttribute) xmlToken.getParent();

    final String fullName = xmlAttribute.getValue();
    if(fullName == null) return resultList;
    abstractEntity= EntityUtils.findEntityAndViewEntityByFullName(psiElement.getProject(),fullName)
            .orElse(null);
    if(abstractEntity == null) return resultList;

    resultList.add(abstractEntity.getXmlElement());
    return resultList;
//    return EntityUtils.getRelatedEntity(psiElement,fullName);

  }

  @Override
  public Icon getNagavitorToIcon() {
    if(abstractEntity instanceof Entity) {
      return EntityUtils.getNagavitorToEntityIcon();
    }else {
      return EntityUtils.getNagavitorToViewIcon();
    }
  }

  @Override
  public String getToolTips() {
    if(abstractEntity instanceof Entity) {
      return EntityUtils.getNagavitorToEntityToolTips();
    }else {
      return EntityUtils.getNagavitorToViewToolTips();
    }

  }


}
