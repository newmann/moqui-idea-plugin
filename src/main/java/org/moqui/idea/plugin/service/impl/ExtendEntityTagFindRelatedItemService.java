package org.moqui.idea.plugin.service.impl;

import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;


import javax.swing.*;
import java.util.List;

import static com.intellij.psi.xml.XmlTokenType.XML_NAME;

public class ExtendEntityTagFindRelatedItemService implements FindRelatedItemService {
  public static ExtendEntityTagFindRelatedItemService INSTANCE = new ExtendEntityTagFindRelatedItemService();

  private ExtendEntityTagFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {


    if(!(psiElement instanceof XmlToken)) return false;
    XmlToken token = (XmlToken) psiElement;
    if(!token.getTokenType().equals(XML_NAME)) return false;
    if(!(token.getText().equals(ExtendEntity.TAG_NAME))) return false;

    if(!(psiElement.getParent() instanceof XmlTag))  return false;

    if(!EntityUtils.isEntitiesFile(psiElement.getContainingFile())) return false;

    return true;

  }

  @Override
  public List<PsiElement> findRelatedItem(PsiElement psiElement) {
//    List<PsiElement> resultList = new ArrayList<>();
    XmlToken xmlToken = (XmlToken) psiElement;
    XmlTag xmlTag =(XmlTag) xmlToken.getParent();

    final String entityName = xmlTag.getAttributeValue(ExtendEntity.ATTR_NAME_ENTITY_NAME);
    final String packageName = xmlTag.getAttributeValue(ExtendEntity.ATTR_NAME_PACKAGE);
    final String fullName = packageName + "." + entityName;

    return EntityUtils.getRelatedEntity(psiElement,fullName);

//    EntityPsiElementService entityPsiElementService =
//            psiElement.getProject().getService(EntityPsiElementService.class);
//
//    DomElement target = entityPsiElementService.getPsiElementByFullName(fullName);
//    if (target == null) {
//      CustomNotifier.warn(psiElement.getProject(), "发现找不到的Entity，fullName：" + fullName +", 所在文件："
//              +psiElement.getContainingFile().getVirtualFile().getPath());
//    }else {
//      resultList.add((PsiElement) target.getXmlElement());
//    }

//    return resultList;
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
