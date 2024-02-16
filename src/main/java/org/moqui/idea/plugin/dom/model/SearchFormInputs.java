package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface SearchFormInputs extends DomElement {
    public static final String TAG_NAME = "search-form-inputs";

    @NotNull
    @SubTag(DefaultParameters.TAG_NAME)
    DefaultParameters getDefaultParameters();


    @NotNull GenericAttributeValue<String> getInputFieldsMap();
    @NotNull GenericAttributeValue<String> getDefaultOrderBy();
    @NotNull GenericAttributeValue<String> getSkipFields();
    @NotNull GenericAttributeValue<String> getPaginate();
    @NotNull GenericAttributeValue<Boolean> getRequireParameters();

}
