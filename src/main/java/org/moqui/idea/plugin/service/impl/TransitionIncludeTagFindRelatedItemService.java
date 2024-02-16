package org.moqui.idea.plugin.service.impl;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;
import org.moqui.idea.plugin.dom.model.SubScreensItem;
import org.moqui.idea.plugin.dom.model.TransitionInclude;
import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.util.ScreenUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.psi.xml.XmlTokenType.XML_NAME;

public class TransitionIncludeTagFindRelatedItemService implements FindRelatedItemService {
  private boolean crudServiceCall = false;

  public static TransitionIncludeTagFindRelatedItemService INSTANCE = new TransitionIncludeTagFindRelatedItemService();

  private TransitionIncludeTagFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {


    if(!(psiElement instanceof XmlToken)) return false;
    XmlToken token = (XmlToken) psiElement;
    if(!token.getTokenType().equals(XML_NAME)) return false;
    if(!(token.getText().equals(TransitionInclude.TAG_NAME))) return false;

    if(!(psiElement.getParent() instanceof XmlTag))  return false;


    if(!(ScreenUtils.isScreenFile(psiElement.getContainingFile())
    )) return false;

    return true;

  }

  @Override
  public List<PsiElement> findRelatedItem(PsiElement psiElement) {

    XmlToken xmlToken = (XmlToken) psiElement;
    XmlTag xmlTag =(XmlTag) xmlToken.getParent();
    if (xmlTag == null) return new ArrayList<>();

    final String location = xmlTag.getAttributeValue(TransitionInclude.ATTR_LOCATION);
    final String name = xmlTag.getAttributeValue(TransitionInclude.ATTR_NAME);

    return ScreenUtils.getRelatedTransitionInclude(psiElement,name,location);

//    Optional<String> optEntityName = EntityUtils.getEntityNameFromServiceCallName(location);
//    if (optEntityName.isEmpty()) {
//      //按查找service的方式查找
//      crudServiceCall = false;
//      return ServiceUtils.getRelatedService(psiElement,location);
//    } else {
//      //按查找entity的方式来查找
//      crudServiceCall = true;
//      return EntityUtils.getRelatedEntity(psiElement,optEntityName.get());
//
//    }

  }

  @Override
  public Icon getNagavitorToIcon() {
    return ScreenUtils.getNagavitorToScreenIcon();
  }

  @Override
  public String getToolTips() {
    return ScreenUtils.getNagavitorToEntityToolTips();
  }

}
