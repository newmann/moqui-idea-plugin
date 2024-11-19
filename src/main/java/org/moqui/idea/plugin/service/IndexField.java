package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.dom.model.AliasAll;
import org.moqui.idea.plugin.util.MyDomUtils;

/**
 * 由于在ViewEntity定义中，AliasAll可以定义prefix，所以需要处理这种情况
 */
public final class IndexField {
    private final String name;
    private final AbstractField abstractField;
    private final String type;
    private final AliasAll aliasAll;


    IndexField(@NotNull AbstractField abstractField, @Nullable AliasAll aliasAll){
        this.abstractField = abstractField;
        this.aliasAll = aliasAll;
        if(aliasAll == null) {
            this.name = MyDomUtils.getValueOrEmptyString(abstractField.getName());
        }else{
            this.name = MyDomUtils.getValueOrEmptyString(aliasAll.getPrefix()) +
                    MyDomUtils.getValueOrEmptyString(abstractField.getName());
        }

        this.type = MyDomUtils.getValueOrEmptyString(abstractField.getType());
    }

    public AbstractField getAbstractField(){
        return this.abstractField;
    }

    public AliasAll getAliasAll() {
        return aliasAll;
    }

    public String getName(){
        return this.name;
    }

    public String getType() {
        return type;
    }


    public boolean isThisField(@NotNull String name){
        return this.name.equals(name);
    }

}
