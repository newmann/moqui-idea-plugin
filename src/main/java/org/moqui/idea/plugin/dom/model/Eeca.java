package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityNameReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.EecaPresentationProvider;

@Presentation(icon = "org.moqui.idea.plugin.MyIcons.EecaTag",provider = EecaPresentationProvider.class)
public interface Eeca extends DomElement {

    public static final String TAG_NAME = "eeca";
//    public static final String ATTR_ID = "id";

    public static final String ATTR_ENTITY = "entity";
//    public static final String ATTR_ON_CREATE = "on-create";
//    public static final String ATTR_ON_UPDATE = "on-update";
//    public static final String ATTR_ON_DELETE = "on-delete";
//    public static final String ATTR_RUN_BEFORE = "run-before";
//    public static final String ATTR_RUN_ON_ERROR = "run-on-error";
//    public static final String ATTR_GET_ENTIRE_ENTITY = "get-entire-entity";
//    public static final String ATTR_GET_ORIGINAL_VALUE = "get-original-value";
//    public static final String ATTR_SET_RESULTS = "set-results";
    @NotNull
//    @Attribute(ATTR_ID)
    GenericAttributeValue<String> getId();

    @NotNull
    @Attribute(ATTR_ENTITY)
//    @Convert(EntityFullNameConverter.class)
    @Referencing(EntityNameReferenceConverter.class)
    GenericAttributeValue<String> getEntity();

    @NotNull
//    @Attribute(ATTR_ON_CREATE)
    GenericAttributeValue<String> getOnCreate();
    @NotNull
//    @Attribute(ATTR_ON_UPDATE)
    GenericAttributeValue<String> getOnUpdate();
    @NotNull
//    @Attribute(ATTR_ON_DELETE)
    GenericAttributeValue<String> getOnDelete();
    @NotNull
//    @Attribute(ATTR_RUN_BEFORE)
    GenericAttributeValue<Boolean> getRunBefore();
    @NotNull
//    @Attribute(ATTR_RUN_ON_ERROR)
    GenericAttributeValue<Boolean> getRunOnError();
    @NotNull
//    @Attribute(ATTR_GET_ENTIRE_ENTITY)
    GenericAttributeValue<Boolean> getGetEntireEntity();
    @NotNull
//    @Attribute(ATTR_GET_ORIGINAL_VALUE)
    GenericAttributeValue<Boolean> getOriginalValue();
    @NotNull
//    @Attribute(ATTR_SET_RESULTS)
    GenericAttributeValue<Boolean> getSetResults();



    @NotNull
    @SubTag(Description.TAG_NAME)
    Description getDescription();

    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();
    @NotNull
    @SubTag(Actions.TAG_NAME)
    Actions getActions();
}
