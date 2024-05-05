package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.*;

public final class IndexEntity {
    private final String entityName;
    private final String packageName;

    private final String shortAlias;
    private final String fullName;
    private final Entity entity;

    private final Map<String,AbstractField> abstractFieldMap = new HashMap<>();

    private final List<ExtendEntity> extendEntityList = new ArrayList<>();

    private final List<Relationship> relationshipList = new ArrayList<>();
    IndexEntity(@NotNull Entity entity){
        this.entity =entity;
        this.entityName = MyDomUtils.getValueOrEmptyString(entity.getEntityName());
        this.packageName = MyDomUtils.getValueOrEmptyString(entity.getPackage());
        this.shortAlias = MyDomUtils.getValueOrEmptyString(entity.getShortAlias());
        if(this.packageName.equals(MyStringUtils.EMPTY_STRING)){
            this.fullName = this.entityName;
        }else {
            this.fullName =  this.packageName + "." + this.entityName;
        }
        updateFieldList();
    }

    public String getFullName(){
        return this.fullName;
    }
    public String getEntityName(){
        return this.entityName;
    }
    public String getPackageName(){
        return this.packageName;
    }
    public Entity getEntity(){return this.entity;}
    public String getShortAlias(){
        return this.shortAlias;
    }
    public List<ExtendEntity> getExtendEntityList(){return this.extendEntityList;}
    public List<Relationship> getRelationshipList(){return this.relationshipList;}

    private void updateFieldList(){

        for(Field field: this.entity.getFieldList()) {
            abstractFieldMap.put(MyDomUtils.getValueOrEmptyString(field.getName()),field);
        }
        relationshipList.addAll(this.entity.getRelationshipList());

        extendEntityList.forEach(item->{
            for(Field field: item.getFieldList()) {
                abstractFieldMap.put(MyDomUtils.getValueOrEmptyString(field.getName()),field);
            }

            relationshipList.addAll(item.getRelationshipList());
        });
    }

    /**
     * 由于在MoquiIndexService中进行了处理，在添加ExtendEntity的时候，只需要简单添加自身的字段即可，不需要进行重新扫描
     * @param extendEntity
     */
    public void AddExtendEntity(ExtendEntity extendEntity){
        if (NotContainExtendEntity(extendEntity)){
            for(Field field: extendEntity.getFieldList()) {
                this.abstractFieldMap.put(MyDomUtils.getValueOrEmptyString(field.getName()),field);
            }
            this.extendEntityList.add(extendEntity);

            this.relationshipList.addAll(extendEntity.getRelationshipList());

        }
    }

    public boolean ContainExtendEntity(ExtendEntity extendEntity){
        for(ExtendEntity item : extendEntityList){
            if(item == extendEntity) return true;
        }
        return false;
    }
    public boolean NotContainExtendEntity(ExtendEntity extendEntity){
        return ! ContainExtendEntity(extendEntity);
    }

    public Optional<List<String>> getFieldNameList(){
        return Optional.of(abstractFieldMap.keySet().stream().toList());

    }
    public Optional<List<AbstractField>> getAbstractFieldList(){
        return Optional.of(abstractFieldMap.values().stream().toList());

    }
    public Optional<List<Field>> getFieldList(){
        List<Field> fieldList = new ArrayList<>();
        for(String key: abstractFieldMap.keySet()){
            fieldList.add((Field) abstractFieldMap.get(key));
        }
        return Optional.of(fieldList);

    }
    public boolean isValid(){
        if(!this.entity.isValid()) return false;
        for(String key: this.abstractFieldMap.keySet()) {
            if(!this.abstractFieldMap.get(key).isValid()) return false;
        }
        for(ExtendEntity item : this.extendEntityList) {
            if(!item.isValid()) return false;
        }
        return true;
    }

    public boolean isThisEntity(@NotNull String name){
        int index = name.lastIndexOf(EntityUtils.ENTITY_NAME_DOT);
        String checkName;
        if(index >=0) {
            checkName = name.substring(index+1);
        }else{
            checkName = name;
        }
        return this.entityName.equals(checkName) || this.shortAlias.equals(checkName);
    }

}
