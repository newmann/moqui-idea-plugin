package org.moqui.idea.plugin.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.AbstractField;
import org.moqui.idea.plugin.dom.model.AliasAll;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

/**
 * 由于在ViewEntity定义中，AliasAll可以定义prefix，所以需要处理这种情况
 */
public final class IndexAbstractField {
    public static IndexAbstractField of(@NotNull AbstractField abstractField, @NotNull AliasAll aliasAll){
        return  new IndexAbstractField(abstractField,aliasAll);
    }
    public static IndexAbstractField of(@NotNull AbstractField abstractField){
        return new IndexAbstractField(abstractField,null);
    }

    private final String name;
    private final AbstractField abstractField;
    private final String type;
    private final AliasAll aliasAll;
    private final String prefix;
    private final String originFieldName;

    IndexAbstractField(@NotNull AbstractField abstractField, @Nullable AliasAll aliasAll){
        this.abstractField = abstractField;
        this.aliasAll = aliasAll;
        this.originFieldName = MyDomUtils.getValueOrEmptyString(abstractField.getName());
        if(aliasAll == null) {
            this.prefix = MyStringUtils.EMPTY_STRING;
        }else{
            this.prefix = MyDomUtils.getValueOrEmptyString(aliasAll.getPrefix());
        }

        this.name = this.prefix + this.originFieldName;

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

    public String getPrefix() {
        return prefix;
    }

    public String getOriginFieldName() {
        return originFieldName;
    }

    public boolean isThisField(@NotNull String name){
        return this.name.equals(name);
    }

}
