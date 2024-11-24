package org.moqui.idea.plugin.service.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlToken;
import org.moqui.idea.plugin.dom.model.Eeca;
import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.util.EecaUtils;
import org.moqui.idea.plugin.util.EntityUtils;

import javax.swing.*;
import java.util.List;

import static com.intellij.psi.xml.XmlTokenType.XML_NAME;

public class EecaTagFindRelatedItemService implements FindRelatedItemService {
  public static EecaTagFindRelatedItemService INSTANCE = new EecaTagFindRelatedItemService();

  private EecaTagFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {
    if(!(psiElement instanceof XmlToken)) return false;
    XmlToken token = (XmlToken) psiElement;
    if(!token.getTokenType().equals(XML_NAME)) return false;
    if(!(token.getText().equals(Eeca.ATTR_ENTITY))) return false;
    PsiElement tokenParent = token.getParent();
    if(tokenParent == null) return false;
    if(!(tokenParent instanceof XmlAttribute))  return false;

    if(!(EecaUtils.isEecasFile(psiElement.getContainingFile())

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
