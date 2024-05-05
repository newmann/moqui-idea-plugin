package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.ParameterPresentationProvider;

import java.util.List;
@Presentation(icon ="MoquiIcons.ParameterTag", provider = ParameterPresentationProvider.class)
public interface Parameter extends AbstractField,AttListParameterGeneral,ParameterValidationsList {
    public static final String TAG_NAME = "parameter";


//    public static final String ATTR_NAME = "name";
//    public static final String ATTR_TYPE = "type";
    public static final String ATTR_FORMAT = "format";
    public static final String ATTR_DEFAULT = "default";
    public static final String ATTR_DEFAULT_VALUE = "default-value";
    public static final String ATTR_ENTITY_NAME = "entity-name";
    public static final String ATTR_FIELD_NAME = "field-name";
    //for form define

    public static final String ATTR_FROM = "from";
    public static final String ATTR_VALUE = "value";


//    @NotNull
//    @Attribute(ATTR_NAME)
//    GenericAttributeValue<String> getName();

//    @NotNull
//    @Attribute(ATTR_TYPE)
//    GenericAttributeValue<String> getType();

    @NotNull
    @Attribute(ATTR_FORMAT)
    GenericAttributeValue<String> getFormat();

    @NotNull
    @Attribute(ATTR_DEFAULT)
    GenericAttributeValue<String> getDefault();

    @NotNull
    @Attribute(ATTR_DEFAULT_VALUE)
    GenericAttributeValue<String> getDefaultValue();

    @NotNull
    @Attribute(ATTR_ENTITY_NAME)
    GenericAttributeValue<String> getEntityName();

    @NotNull
    @Attribute(ATTR_FIELD_NAME)
    GenericAttributeValue<String> getFieldName();

    @NotNull
    @Attribute(ATTR_FROM)
    GenericAttributeValue<String> getFrom();
    @NotNull
    @Attribute(ATTR_VALUE)
    GenericAttributeValue<String> getValue();


    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();


    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull
    @SubTagList(AutoParameters.TAG_NAME)
    List<AutoParameters> getAutoParametersList();


}
