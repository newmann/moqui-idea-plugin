package org.moqui.idea.plugin.service.impl;

import org.moqui.idea.plugin.dom.model.MemberEntity;
import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlToken;

import javax.swing.*;
import java.util.List;

import static com.intellij.psi.xml.XmlTokenType.XML_NAME;

public class MemberEntityTagFindRelatedItemService implements FindRelatedItemService {
  public static MemberEntityTagFindRelatedItemService INSTANCE = new MemberEntityTagFindRelatedItemService();

  private MemberEntityTagFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {
    if(!(psiElement instanceof XmlToken)) return false;
    XmlToken token = (XmlToken) psiElement;
    if(!token.getTokenType().equals(XML_NAME)) return false;
    if(!(token.getText().equals(MemberEntity.ATTR_ENTITY_NAME))) return false;

    //验证是否在entity-name这个attribute下
    if(!(psiElement.getParent() instanceof XmlAttribute))  return false;
    //验证是否member-entity这个tag下
    if(!(psiElement.getParent().getParent() instanceof XmlTag))  return false;
    if(!(((XmlTag)psiElement.getParent().getParent()).getName().equals(MemberEntity.TAG_NAME) ))  return false;

    if(!EntityUtils.isEntitiesFile(psiElement.getContainingFile())) return false;
    return true;
  }

  @Override
  public List<PsiElement> findRelatedItem(PsiElement psiElement) {
//    List<PsiElement> resultList = new ArrayList<>();
    XmlToken xmlToken = (XmlToken) psiElement;
    XmlAttribute xmlAttribute =(XmlAttribute) xmlToken.getParent();

    final String fullName = xmlAttribute.getValue();
    return EntityUtils.getRelatedEntity(psiElement,fullName);

//    EntityPsiElementService entityPsiElementService =
//            psiElement.getProject().getService(EntityPsiElementService.class);
//    DomElement target = entityPsiElementService.getPsiElementByFullName(fullName);
//    if (target == null) {
//      CustomNotifier.warn(psiElement.getProject(), "发现找不到的Entity，fullName：" + fullName + ", 所在文件："
//              + psiElement.getContainingFile().getVirtualFile().getPath());
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
