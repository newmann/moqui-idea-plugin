package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractEntity;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractIndexEntity extends AbstractIndex {
    protected  String shortName = MyStringUtils.EMPTY_STRING;
    protected  String packageName = MyStringUtils.EMPTY_STRING;
    protected String shortAlias = MyStringUtils.EMPTY_STRING;
    protected  String fullName = MyStringUtils.EMPTY_STRING;
    protected Map<String, AbstractField> abstractFieldMap;

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
    public Optional<List<AbstractField>> getAbstractFieldList(){
        return Optional.of(this.abstractFieldMap.values().stream().toList());
    }
    public Optional<List<String>> getFieldNameList(){
        return Optional.of(abstractFieldMap.keySet().stream().toList());

    }

    public AbstractEntity getAbstractEntity() {
        return abstractEntity;
    }
}
