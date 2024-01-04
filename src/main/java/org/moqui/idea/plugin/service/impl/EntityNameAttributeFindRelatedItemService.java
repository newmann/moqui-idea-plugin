package org.moqui.idea.plugin.service.impl;

import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ScreenUtils;
import org.moqui.idea.plugin.util.ServiceUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlToken;

import javax.swing.*;
import java.util.List;

import static com.intellij.psi.xml.XmlTokenType.XML_NAME;

public class EntityNameAttributeFindRelatedItemService implements FindRelatedItemService {
  public static EntityNameAttributeFindRelatedItemService INSTANCE = new EntityNameAttributeFindRelatedItemService();

  private EntityNameAttributeFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {
    if(!(psiElement instanceof XmlToken)) return false;
    XmlToken token = (XmlToken) psiElement;
    if(!token.getTokenType().equals(XML_NAME)) return false;
    if(!(token.getText().equals("entity-name"))) return false;
    PsiElement tokenParent = token.getParent();
    if(tokenParent == null) return false;
    if(!(tokenParent instanceof XmlAttribute))  return false;

    if(!(ServiceUtils.isServicesFile(psiElement.getContainingFile())
            || ScreenUtils.isScreenFile(psiElement.getContainingFile())
    )) return false;

    return true;
  }

  @Override
  public List<PsiElement> findRelatedItem(PsiElement psiElement) {
//    List<PsiElement> resultList = new ArrayList<>();
    XmlToken xmlToken = (XmlToken) psiElement;
    XmlAttribute xmlAttribute =(XmlAttribute) xmlToken.getParent();

    final String fullName = xmlAttribute.getValue();
    return EntityUtils.getRelatedEntity(psiElement,fullName);

  }

  @Override
  public Icon getNagavitorToIcon() {
    return EntityUtils.getNagavitorToEntityIcon();
  }

  @Override
  public String getToolTips() {
    return EntityUtils.getNagavitorToEntityToolTips();
  }


}
