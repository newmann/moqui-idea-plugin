package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ComplexAlias extends DomElement {
    
    public static final String TAG_NAME = "complex-alias";


    @NotNull GenericAttributeValue<String> getOperator();
    @NotNull GenericAttributeValue<String> getFunction();
    @NotNull GenericAttributeValue<String> getExpression();


    @NotNull
    @SubTagList(ComplexAlias.TAG_NAME)
    List<ComplexAlias> getComplexAliasList();

    @NotNull
    @SubTagList(ComplexAliasField.TAG_NAME)
    List<ComplexAliasField> getComplexAliasFieldList();


}
