package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.*;

public final class IndexViewEntity {
    private final String viewName;
    private final String packageName;


    private final String fullName;
    private final ViewEntity viewEntity;

    private Map<String, AbstractField> abstractFieldMap;


    public IndexViewEntity(@NotNull ViewEntity viewEntity){
        this.viewEntity = viewEntity;
        this.viewName = MyDomUtils.getValueOrEmptyString(viewEntity.getEntityName());
        this.packageName = MyDomUtils.getValueOrEmptyString(viewEntity.getPackage());
        if(this.packageName.equals(MyStringUtils.EMPTY_STRING)){
            this.fullName = this.viewName;
        }else {
            this.fullName =  this.packageName + "." + this.viewName;
        }

//        updateFieldList();
    }

    public String getFullName(){
        return this.fullName;
    }
    public String getViewName(){
        return this.viewName;
    }
    public String getPackageName(){
        return this.packageName;
    }
    public ViewEntity getViewEntity(){return this.viewEntity;}

//    private void updateFieldList(){
//        //重置所有的字段
//        this.fieldMap = new HashMap<>();
//        //todo 需要重新编写
//        List<AbstractField> fieldList = EntityUtils.getViewEntityFieldList(Objects.requireNonNull(this.viewEntity.getXmlElement()).getProject(),this.viewEntity);
//
//        for(AbstractField field: fieldList) {
//            fieldMap.put(MyDomUtils.getValueOrEmptyString(field.getName()),field);
//        }
//
//    }



    public Optional<List<String>> getFieldNameList(){
        return Optional.of(abstractFieldMap.keySet().stream().toList());

    }
    public Optional<List<AbstractField>> getAbstractFieldList(){
        return Optional.of(abstractFieldMap.values().stream().toList());

    }
    public void setAbstractFieldMap(Map<String,AbstractField> abstractFieldMap){
        this.abstractFieldMap = abstractFieldMap;
    }
    public Map<String,AbstractField> getAbstractFieldMap(){
        return this.abstractFieldMap;
    }

    public boolean isValid(){
        if(!this.viewEntity.isValid()) return false;
        for(String key: this.abstractFieldMap.keySet()) {
            if(!this.abstractFieldMap.get(key).isValid()) return false;
        }
        return true;
    }

    public boolean isThisViewEntity(@NotNull String name){
        //如果帶点，就按全名匹配，如果不带点，就按名称匹配
        int index = name.lastIndexOf(EntityUtils.ENTITY_NAME_DOT);
        if(index >=0) {
            return this.fullName.equals(name);
        }else{
            return this.viewName.equals(name);
        }

    }
}
