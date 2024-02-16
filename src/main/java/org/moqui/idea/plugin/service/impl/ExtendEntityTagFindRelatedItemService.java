package org.moqui.idea.plugin.service.impl;

import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.moqui.idea.plugin.util.MyDomUtils;


import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExtendEntityTagFindRelatedItemService implements FindRelatedItemService {
  public static ExtendEntityTagFindRelatedItemService INSTANCE = new ExtendEntityTagFindRelatedItemService();

  private ExtendEntityTagFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {


//    if(!(psiElement instanceof XmlToken)) return false;
//    XmlToken token = (XmlToken) psiElement;
//    if(!token.getTokenType().equals(XML_NAME)) return false;

    if(MyDomUtils.isNotTagName(psiElement)) return false;

//    if(!(token.getText().equals(ExtendEntity.TAG_NAME))) return false;
    Optional<String> optTagName = MyDomUtils.getCurrentTagName(psiElement);
    if(optTagName.isEmpty()) return false;

    if(!optTagName.get().equals(ExtendEntity.TAG_NAME)) return false;

//    if(!MyDomUtils.getTagName(psiElement).equals(ExtendEntity.TAG_NAME)) return false;

//    if(!(psiElement.getParent() instanceof XmlTag))  return false;

    if(!EntityUtils.isEntitiesFile(psiElement.getContainingFile())) return false;

    return true;

  }

  @Override
  public List<PsiElement> findRelatedItem(PsiElement psiElement) {
//    List<PsiElement> resultList = new ArrayList<>();
    if(!(psiElement instanceof XmlTag xmlTag)) return new ArrayList<>();

    if (!xmlTag.isValid()) return new ArrayList<>();

    final String entityName = xmlTag.getAttributeValue(ExtendEntity.ATTR_ENTITY_NAME);
    final String packageName = xmlTag.getAttributeValue(ExtendEntity.ATTR_PACKAGE);
    final String fullName = packageName + "." + entityName;

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
