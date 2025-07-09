package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityNameReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.EecaPresentationProvider;

@Presentation(icon = "org.moqui.idea.plugin.MyIcons.EecaTag",provider = EecaPresentationProvider.class)
public interface Eeca extends DomElement {

    String TAG_NAME = "eeca";
//    String ATTR_ID = "id";

    String ATTR_ENTITY = "entity";
//    String ATTR_ON_CREATE = "on-create";
//    String ATTR_ON_UPDATE = "on-update";
//    String ATTR_ON_DELETE = "on-delete";
//    String ATTR_RUN_BEFORE = "run-before";
//    String ATTR_RUN_ON_ERROR = "run-on-error";
//    String ATTR_GET_ENTIRE_ENTITY = "get-entire-entity";
//    String ATTR_GET_ORIGINAL_VALUE = "get-original-value";
//    String ATTR_SET_RESULTS = "set-results";
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
