package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.*;

public final class IndexViewEntity extends AbstractIndexEntity {
//    private final String viewName;
//    private final String packageName;
//
//
//    private final String fullName;
    private ViewEntity viewEntity;

//    private Map<String, AbstractField> abstractFieldMap;

    /**
     * ViewEntity包含的所有IndexEntity
     * String 表示entityAlias
     * IndexEntity 表示对应的最终indexEntity，MemberRelationship也是经过解析之后的
     */
    private Map<String,AbstractIndexEntity> abstractIndexEntityMap;

    public IndexViewEntity(@NotNull ViewEntity viewEntity){
        this.viewEntity = viewEntity;
        this.abstractEntity = viewEntity;

        RefreshViewEntity();
//        updateFieldList();
    }
    public void RefreshViewEntity(){
        this.shortName = MyDomUtils.getValueOrEmptyString(viewEntity.getEntityName());
        this.packageName = MyDomUtils.getValueOrEmptyString(viewEntity.getPackage());
        if(this.packageName.equals(MyStringUtils.EMPTY_STRING)){
            this.fullName = this.shortName;
        }else {
            this.fullName =  this.packageName + "." + this.shortName;
        }

    }

//    public String getFullName(){
//        return this.fullName;
//    }
//    public String getViewName(){
//        return this.viewName;
//    }
//    public String getPackageName(){
//        return this.packageName;
//    }
    public ViewEntity getViewEntity(){return this.viewEntity;}

    public void setViewEntity(ViewEntity viewEntity){
        this.viewEntity = viewEntity;
        this.abstractEntity = viewEntity;
    }




//    public Optional<List<String>> getFieldNameList(){
//        return Optional.of(abstractFieldMap.keySet().stream().toList());
//
//    }
//    public Optional<List<AbstractField>> getAbstractFieldList(){
//        return Optional.of(abstractFieldMap.values().stream().toList());
//
//    }
    public void setAbstractFieldMap(Map<String, IndexAbstractField> abstractFieldMap){
        this.abstractFieldMap = abstractFieldMap;
    }
    public Map<String, IndexAbstractField> getAbstractFieldMap(){
        return this.abstractFieldMap;
    }

//    public boolean isValid(){
//        if(!this.viewEntity.isValid()) return false;
//        for(String key: this.abstractFieldMap.keySet()) {
//            if(!this.abstractFieldMap.get(key).isValid()) return false;
//        }
//        return true;
//    }


    public Map<String, AbstractIndexEntity> getMemberIndexEntityMap() {
        return abstractIndexEntityMap;
    }

    public void setAbstractIndexEntityMap(Map<String, AbstractIndexEntity> abstractIndexEntityMap) {
        this.abstractIndexEntityMap = abstractIndexEntityMap;
    }

    public void addAbstractIndexEntity(@NotNull String alias, @NotNull AbstractIndexEntity index) {
        this.abstractIndexEntityMap.put(alias, index);
    }

    public Optional<AbstractIndexEntity> getAbstractIndexEntityByAlias(@NotNull String alias){
        return Optional.ofNullable(this.abstractIndexEntityMap.get(alias));
    }

}
