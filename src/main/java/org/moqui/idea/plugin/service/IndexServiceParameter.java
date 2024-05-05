package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.MyDomUtils;

import java.util.*;

public final class IndexServiceParameter {
    private final String parameterName;
    private final AbstractField abstractField;

    private Map<String,IndexServiceParameter> childParameterMap = new HashMap<>();

    IndexServiceParameter(AbstractField abstractField){
        this.abstractField = abstractField;
        this.parameterName = MyDomUtils.getValueOrEmptyString(abstractField.getName());
    }

    public AbstractField getAbstractField(){
        return this.abstractField;
    }
    public String getParameterName(){
        return this.parameterName;
    }
    public List<IndexServiceParameter> getChildParameterList(){return this.childParameterMap.values().stream().toList();}
    public Map<String, IndexServiceParameter> getChildParameterMap(){return this.childParameterMap;}

    public void setChildParameterList(Map<String,IndexServiceParameter> childParameterMap){
        this.childParameterMap = childParameterMap;
    }

    public boolean isThisParameter(@NotNull String name){
        return this.parameterName.equals(name);
    }

}
