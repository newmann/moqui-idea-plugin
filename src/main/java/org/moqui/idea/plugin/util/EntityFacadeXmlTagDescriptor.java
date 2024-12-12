package org.moqui.idea.plugin.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.dom.model.Relationship;
import org.moqui.idea.plugin.service.IndexEntity;

import java.util.Optional;

/**
 * 在文件entity-facade-xml中，根据当前的PsiElement查找所在的entityName
 * 查找逻辑：
 * 1、在entity-facade-xml下的第一级Tag，tagName就是EntityName（可能是缩写）
 * 2、第二级Tag，如果是全名或第一个字母是大写的，tagName就是EntityName。如果第一个字母是小写，有可能是第一级Tag的relationship的shortAlias，如果不是，就直接找Entity，是Entity的shortAlias
 * 3、不会跨级查找，即下一级Tag名要么是上一级Tag的relationship的shortAlias，要么直接就是EntityName或Entity的shortAlias
 * 4、delete-开头的，是指删除对应entity的数据
 */
public class EntityFacadeXmlTagDescriptor {
    public static EntityFacadeXmlTagDescriptor of(@NotNull PsiElement psiElement){
        return new EntityFacadeXmlTagDescriptor(psiElement);
    }

    EntityFacadeXmlTagDescriptor(@NotNull PsiElement psiElement){
        if(psiElement instanceof XmlTag){
            myCurTag = (XmlTag) psiElement;
        }else {
            myCurTag = MyDomUtils.getParentTag(psiElement).orElse(null);
        }

        if(myCurTag == null) {
            myIsValid = false;
            return;
        }

        myParentTag = myCurTag.getParentTag();
        if(myParentTag == null){
            myIsValid = false;
            return;
        }
        String curTagName = myCurTag.getName();
        if (curTagName.startsWith(MyStringUtils.ENTITY_FACADE_DELETE_TAG))
            curTagName = curTagName.substring(MyStringUtils.ENTITY_FACADE_DELETE_TAG.length());

        myEntityName = curTagName;

        if(!myParentTag.getName().equals(EntityFacadeXml.TAG_NAME)) {
            if (getIsShortAlias()) {
                //进一步判断是否为ParentTag的relationship
                IndexEntity indexEntity = EntityUtils.getIndexEntityByName(psiElement.getProject(), myParentTag.getName()).orElse(null);
                if (indexEntity != null) {
                    myRelationship = indexEntity.getRelationshipByName(curTagName).orElse(null);
                    if (myRelationship != null) {
                        myIsRelationship = true;
                        myEntityName = MyDomUtils.getValueOrEmptyString(myRelationship.getRelated());
                    }
                }
            }
        }
    }
    private final XmlTag myCurTag;
    private XmlTag myParentTag;

    private Relationship myRelationship;
    private boolean myIsValid = true;
    private boolean myIsRelationship = false;
    private String myEntityName = MyStringUtils.EMPTY_STRING;

    public String getEntityName() {
        return myEntityName;
    }

    public boolean getIsValid() {
        return myIsValid;
    }

    public Relationship getRelationship() {
        return myRelationship;
    }

    public boolean getIsShortAlias(){
        return !myEntityName.contains(".") && !MyStringUtils.firstCharIsUpperCase(myEntityName) && !myIsRelationship ;
    }

    public boolean getIsRelationship() {
        return myIsRelationship;
    }
    public String getTagName(){
        if(myCurTag != null) {
            return myCurTag.getName();
        }else{
            return null;
        }
    }
}
