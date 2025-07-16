package org.moqui.idea.plugin.util;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Field;
import org.moqui.idea.plugin.dom.model.Relationship;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.service.IndexService;
import org.moqui.idea.plugin.service.MoquiIndexService;

import java.util.ArrayList;
import java.util.List;

/**
 * 在文件entity-facade-xml中，根据当前的PsiElement查找所在的entityName
 * 查找逻辑：
 * 1、在entity-facade-xml下的第一级Tag，tagName就是EntityName（可能是缩写）
 * 2、第二级Tag，如果是全名或第一个字母是大写的，tagName就是EntityName。
 *  如果第一个字母是小写，有可能是第一级Tag的relationship的shortAlias，有可能是Entity的字段，如果都不是，就直接找Entity，是Entity的shortAlias
 * 3、不会跨级查找，即下一级Tag名要么是上一级Tag的relationship的shortAlias，要么直接就是EntityName或Entity的shortAlias
 * 4、delete-开头的，是指删除对应entity的数据
 * 5.有可能是ServiceCall，属性为service 的 in parameter
 */
public class EntityFacadeXmlTagDescriptor {
    public static EntityFacadeXmlTagDescriptor of(@NotNull PsiElement psiElement){
        XmlTag curTag = MyDomUtils.getParentTag(psiElement).orElse(null);

        if(curTag == null || EntityFacadeXmlUtils.isEntityFacadeRootTag(curTag)) {
            return new EntityFacadeXmlTagDescriptor();
        }

        EntityFacadeXmlTagDescriptor saved = EntityFacadeXmlUtils.getFacadeEntityFromXmlTag(curTag).orElse(null);
        if(saved != null && saved.entityIsValid() && saved.getEntityName().equals(curTag.getName())) return saved;

        return new EntityFacadeXmlTagDescriptor(psiElement);
    }
    private final static Logger LOGGER = Logger.getInstance(EntityFacadeXmlTagDescriptor.class);
    private EntityFacadeXmlTagDescriptor() {
        myCurTag = null;
        myProject = null;
        myIsValid = false;
        myRelationship = null;
        myField = null;
        myEntityName = MyStringUtils.EMPTY_STRING;
    }

    private EntityFacadeXmlTagDescriptor(@NotNull PsiElement psiElement){

        myCurTag = MyDomUtils.getParentTag(psiElement).orElse(null);

        if(myCurTag == null || EntityFacadeXmlUtils.isEntityFacadeRootTag(myCurTag)) {
            myProject = null;
            myIsValid = false;
            myRelationship = null;
            myField = null;
            myEntityName = MyStringUtils.EMPTY_STRING;
            return ;
        }

        //将当前curTag的EntityFacadeXmlTagDescriptor删除
        EntityFacadeXmlUtils.putFacadeEntityToXmlTag(myCurTag,null);

        this.myProject = psiElement.getProject();
        this.myEntityXmlFileLastUpdatedStamp = myProject.getService(MoquiIndexService.class).getEntityXmlFileLastUpdatedStamp();

        //将所有无效parentTag重新扫描
        List<XmlTag> updateXmlTag = new ArrayList<>();
        XmlTag parentTag = MyDomUtils.getTagParentTag(myCurTag).orElse(null);
        EntityFacadeXmlTagDescriptor savedDescriptor = null;
        while(true) {
            if (parentTag == null) {
                myIsValid = false;
                break;
            }
            if(EntityFacadeXmlUtils.isEntityFacadeRootTag(parentTag)) break;

            savedDescriptor = EntityFacadeXmlUtils.getFacadeEntityFromXmlTag(parentTag).orElse(null);
            if(savedDescriptor != null && savedDescriptor.entityIsValid()) break;

            updateXmlTag.add(parentTag);
            parentTag = MyDomUtils.getTagParentTag(parentTag).orElse(null);

        }
        for(int i= updateXmlTag.size()-1; i>=0; i--) {
            XmlTag curTag = updateXmlTag.get(i);
            savedDescriptor = of(curTag);
            if(savedDescriptor.entityIsValid()) {
                EntityFacadeXmlUtils.putFacadeEntityToXmlTag(curTag,savedDescriptor);
            }else{
                myIsValid = false;
                return;
            }
        }

        String curTagName = myCurTag.getName();
        if (curTagName.startsWith(MyStringUtils.ENTITY_FACADE_DELETE_TAG))
            curTagName = curTagName.substring(MyStringUtils.ENTITY_FACADE_DELETE_TAG.length());

        myEntityName = curTagName;

        if(MyStringUtils.isEmpty(myEntityName) ) {
            myIsValid = false;
            return;
        }


        if (maybeShortAlias(myEntityName)) {
            //进一步判断是否为ParentTag的relationship或字段
//                IndexEntity indexEntity = EntityUtils.getIndexEntityByName(psiElement.getProject(), myParentTag.getName()).orElse(null);
            //直接取myParentTag的name有问题，因为这个名称可能是relationship的名称，不是entity的名称。该从保存的自定义数据中获取，该数据是在referenc时写入

            IndexEntity indexEntity = savedDescriptor == null ? null : EntityUtils.getIndexEntityByName(psiElement.getProject(), savedDescriptor.getEntityName()).orElse(null);
            if (indexEntity != null) {
                myRelationship = indexEntity.getRelationshipByName(curTagName).orElse(null);
                if (myRelationship == null) {
                    myField = indexEntity.getFieldByName(curTagName).orElse(null);
                    if(myField != null) {
                        //如果是Field，则myEntityName就是当前的Field的EntityName
                        myEntityName = savedDescriptor.getEntityName();
                    }
                }else{
                    myIsRelationship = true;
                    myEntityName = MyDomUtils.getValueOrEmptyString(myRelationship.getRelated());
                }
            }
        }

        //判断当前的myEntityName是否是一个有效的EntityName
        IndexEntity indexEntity = EntityUtils.getIndexEntityByName(psiElement.getProject(), myEntityName).orElse(null);

        myTagType = EntityFacadeXmlTagType.Entity;
        if(indexEntity == null) {
            myIsValid = false;
            //判断当前是否为ServiceCall
            final String serviceName = ServiceUtils.normalizeServiceName(psiElement.getProject(),myCurTag.getName()).orElse(null);
            if(serviceName != null) {
                IndexService indexService = ServiceUtils.getIndexService(psiElement.getProject(), serviceName).orElse(null);
                if (indexService != null) {
                    LOGGER.warn("EntityFacadeXmlTagDescriptor: Entity not found for tag {" + myCurTag.getName() + "}, but found service");
                    myIsValid = true;
                    myTagType = EntityFacadeXmlTagType.Service;
                    myServiceCallName = serviceName;
                    this.myServiceXmlFileLastUpdatedStamp = myProject.getService(MoquiIndexService.class).getServiceXmlFileLastUpdatedStamp();
                }
            }

            if(!myIsValid) return;
        }

        //将当前实例写入xmlTag
        EntityFacadeXmlUtils.putFacadeEntityToXmlTag(myCurTag,this);

    }
    private EntityFacadeXmlTagType myTagType = EntityFacadeXmlTagType.Entity;
    private final XmlTag myCurTag;
    private final Project myProject;
    private Relationship myRelationship;
    private boolean myIsValid = true;
    private boolean myIsRelationship = false;
    private String myEntityName = MyStringUtils.EMPTY_STRING;
    private long myEntityXmlFileLastUpdatedStamp = 0L;
    private long myServiceXmlFileLastUpdatedStamp = 0L;
    private Field myField = null;


    private String myServiceCallName = MyStringUtils.EMPTY_STRING;



    public EntityFacadeXmlTagType getTagType() {
        return myTagType;
    }

    public String getServiceCallName() {
        return myServiceCallName;
    }

    public String getEntityName() {
        return myEntityName;
    }

    public boolean entityIsValid() {
        return myIsValid && entityXmlFileIsNotUpdated();
    }

    public boolean entityXmlFileIsNotUpdated(){
        return myProject != null && myProject.getService(MoquiIndexService.class).getEntityXmlFileLastUpdatedStamp() == this.myEntityXmlFileLastUpdatedStamp;
    }
    public boolean serviceIsValid() {
        return myIsValid && serviceXmlFileIsNotUpdated();
    }
    public boolean serviceXmlFileIsNotUpdated(){
        return myProject != null && myProject.getService(MoquiIndexService.class).getServiceXmlFileLastUpdatedStamp() == this.myServiceXmlFileLastUpdatedStamp;
    }
    public Relationship getRelationship() {
        return myRelationship;
    }

    public boolean getIsShortAlias(){
        return maybeShortAlias(getEntityName()) && !getIsRelationship() && !getIsField();
    }

    private boolean maybeShortAlias(@NotNull String checkStr){
        return !checkStr.contains(".") && !MyStringUtils.firstCharIsUpperCase(checkStr);
    }
    public boolean getIsField() {
        return myField != null;
    }

    public Field getField() {
        return myField;
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
