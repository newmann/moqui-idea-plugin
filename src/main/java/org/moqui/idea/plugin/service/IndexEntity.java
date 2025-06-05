package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.*;

public final class IndexEntity extends AbstractIndexEntity {

    private Entity entity;



    private List<ExtendEntity> extendEntityList;

    private List<Relationship> relationshipList = new ArrayList<>();
    IndexEntity(@NotNull Entity entity,@NotNull List<ExtendEntity> extendEntityList){
        this.entity =entity;
        this.abstractEntity = entity;

//        setMainDomElement(entity);
        this.extendEntityList = extendEntityList;

        this.RefreshEntity();

        this.setLastRefreshStamp(System.currentTimeMillis());

    }

    public void RefreshEntity(){
        this.shortName = MyDomUtils.getValueOrEmptyString(entity.getEntityName());
        this.packageName = MyDomUtils.getValueOrEmptyString(entity.getPackage());
        this.shortAlias = MyDomUtils.getValueOrEmptyString(entity.getShortAlias());
        if(this.packageName.equals(MyStringUtils.EMPTY_STRING)){
            this.fullName = this.shortName;
        }else {
            this.fullName =  this.packageName + "." + this.shortName;
        }

        this.indexAbstractFieldMap = new HashMap<>();

        for(Field field: this.entity.getFieldList()) {
            indexAbstractFieldMap.put(MyDomUtils.getValueOrEmptyString(field.getName()), IndexAbstractField.of(this,this,field));
        }
        this.relationshipList = new ArrayList<>();
        relationshipList.addAll(this.entity.getRelationshipList());

        for(ExtendEntity extendEntity : extendEntityList){
            for(Field field: extendEntity.getFieldList()) {
                this.indexAbstractFieldMap.put(MyDomUtils.getValueOrEmptyString(field.getName()), IndexAbstractField.of(this,this,field));
            }
            this.relationshipList.addAll(extendEntity.getRelationshipList());
        }


    }
    public Entity getEntity(){return this.entity;}
    public void setEntity(Entity entity){
        this.entity = entity;
        this.abstractEntity = entity;}

    public String getShortAlias(){
        return this.shortAlias;
    }
    public List<ExtendEntity> getExtendEntityList(){return this.extendEntityList;}
    public List<Relationship> getRelationshipList(){return this.relationshipList;}

    public void setExtendEntityList(List<ExtendEntity> extendEntityList) {
        this.extendEntityList = extendEntityList;
    }

    //    private void RefreshExtendEntity(){
//        XmlElement entityXmlElement = this.entity.getXmlElement();
//        if(entityXmlElement == null){return;}
//        List<DomFileElement<Entities>> fileElementList = MyDomUtils.findDomFileElementsByRootClass(entityXmlElement.getProject(),Entities.class);
//
//    }
//    /**
//     * 由于在MoquiIndexService中进行了处理，在添加ExtendEntity的时候，只需要简单添加自身的字段即可，不需要进行重新扫描
//     * @param extendEntity
//     */
//    public void AddExtendEntity(ExtendEntity extendEntity){
//        if (NotContainExtendEntity(extendEntity)){
//            this.extendEntityList.add(extendEntity);
//        }
//    }
//
//    public boolean ContainExtendEntity(ExtendEntity extendEntity){
//        for(ExtendEntity item : extendEntityList){
//            if(item == extendEntity) return true;
//        }
//        return false;
//    }
//
//    public boolean NotContainExtendEntity(ExtendEntity extendEntity){
//        return ! ContainExtendEntity(extendEntity);
//    }


    public @NotNull List<Field> getFieldList(){
        List<Field> fieldList = new ArrayList<>();
        for(String key: indexAbstractFieldMap.keySet()){
            fieldList.add((Field) indexAbstractFieldMap.get(key).getAbstractField());
        }
        return fieldList;

    }
    public Optional<Field> getFieldByName(@NotNull String fieldName){
        return Optional.ofNullable((Field)indexAbstractFieldMap.get(fieldName).getAbstractField());
    }

    public Optional<Relationship> getRelationshipByName(@NotNull String relationshipName){
        return relationshipList.stream()
                .filter(item->EntityUtils.isThisRelationshipRelatedName(item,relationshipName))
                .findFirst();
    }

}
