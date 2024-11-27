package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractEntity;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractIndexEntity extends AbstractIndex {
    protected  String shortName = MyStringUtils.EMPTY_STRING;
    protected  String packageName = MyStringUtils.EMPTY_STRING;
    protected String shortAlias = MyStringUtils.EMPTY_STRING;
    protected  String fullName = MyStringUtils.EMPTY_STRING;
//    protected Map<String, AbstractField> abstractFieldMap;
    protected Map<String, IndexAbstractField> indexAbstractFieldMap;
    protected AbstractEntity abstractEntity;

    public String getFullName(){
        return this.fullName;
    }
    public String getShortName(){
        return this.shortName;
    }
    public String getPackageName(){
        return this.packageName;
    }

    public boolean isThisEntity(@NotNull String name){

        return this.fullName.equals(name) || this.shortName.equals(name) || this.shortAlias.equals(name);

    }
    public @NotNull List<IndexAbstractField> getIndexAbstractFieldList(){
        if(this.indexAbstractFieldMap == null) {
            return new ArrayList<>();
        }else {
            return new ArrayList<>(this.indexAbstractFieldMap.values());
        }
    }
    public @NotNull List<AbstractField> getAbstractFieldList(){
        if(this.indexAbstractFieldMap == null) {
            return new ArrayList<>();
        }else {
            return this.indexAbstractFieldMap.values().stream().map(IndexAbstractField::getAbstractField).toList();
        }
    }

    public @NotNull List<String> getFieldNameList(){
        if(this.indexAbstractFieldMap == null) return new ArrayList<>();

        return indexAbstractFieldMap.keySet().stream().toList();

    }

    public @NotNull Map<String,IndexAbstractField> getIndexAbstractFieldMap(){
        if(this.indexAbstractFieldMap == null ) return new HashMap<>();
        return indexAbstractFieldMap;
    }

    public Optional<AbstractEntity> getAbstractEntity() {
        return Optional.ofNullable(abstractEntity);
    }
}
