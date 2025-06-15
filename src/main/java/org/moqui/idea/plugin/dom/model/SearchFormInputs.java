package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.MultiEntityFieldNameReferenceConverter;

public interface SearchFormInputs extends DomElement {
    
    public static final String TAG_NAME = "search-form-inputs";

    
    public static final String ATTR_DEFAULT_ORDER_BY = "default-order-by";
    @NotNull
    @SubTag(DefaultParameters.TAG_NAME)
    DefaultParameters getDefaultParameters();


    @NotNull GenericAttributeValue<String> getInputFieldsMap();

    @NotNull
    @Attribute(ATTR_DEFAULT_ORDER_BY)
    @Referencing(MultiEntityFieldNameReferenceConverter.class)
    GenericAttributeValue<String> getDefaultOrderBy();

    @NotNull GenericAttributeValue<String> getSkipFields();
    @NotNull GenericAttributeValue<String> getPaginate();
    @NotNull GenericAttributeValue<Boolean> getRequireParameters();

}
