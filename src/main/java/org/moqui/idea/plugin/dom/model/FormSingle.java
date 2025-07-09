package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.FieldRefReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.FormSinglePresentationProvider;

import java.util.List;
@Presentation(provider = FormSinglePresentationProvider.class)
public interface FormSingle extends AbstractForm {
    
    String TAG_NAME = "form-single";

//    String ATTR_NAME = "name";
//    String ATTR_EXTENDS = "extends";
    
    String ATTR_OWNER_FORM = "owner-name";

//    String ATTR_TRANSITION = "transition";
    String ATTR_MAP = "map";
    
    String ATTR_FOCUS_FIELD = "focus-field";

    
    String ATTR_SKIP_START = "skip-start";
    
    String ATTR_SKIP_END = "skip-end";

    
    String ATTR_DYNAMIC = "skip-dynamic";
    
    String ATTR_BACKGROUND_SUBMIT = "background-submit";
    
    String ATTR_BACKGROUND_RELOAD_ID = "background-reload-id";
    
    String ATTR_BACKGROUND_HIDE_ID = "background-hide-id";
    
    String ATTR_BACKGROUND_MESSAGE = "background-message";
    
    String ATTR_SERVER_STATIC = "server-static";
    
    String ATTR_BODY_PARAMETERS = "body-parameters";
    
    String ATTR_PASS_THROUGH_PARAMETERS = "pass-through-parameters";
    
    String ATTR_EXCLUDE_EMPTY_FIELDS = "exclude-empty-fields";
//    @NotNull
//    @Attribute(ATTR_NAME)
//    GenericAttributeValue<String> getName();
//    @NotNull
//    @Attribute(ATTR_EXTENDS)
//    @Convert(LocationConverter.class)
//    GenericAttributeValue<String> getExtends();
    @NotNull
    @Attribute(ATTR_OWNER_FORM)
    GenericAttributeValue<String> getOwnerForm();
//    @NotNull
//    @Attribute(ATTR_TRANSITION)
//    @Convert(TransitionConverter.class)
//    GenericAttributeValue<String> getTransition();
    @NotNull
    @Attribute(ATTR_MAP)
    GenericAttributeValue<String> getMap();
    @NotNull
    @Attribute(ATTR_FOCUS_FIELD)
    @Referencing(FieldRefReferenceConverter.class)
    GenericAttributeValue<String> getFocusField();
    @NotNull
    @Attribute(ATTR_SKIP_START)
    GenericAttributeValue<String> getSkipStart();
    @NotNull
    @Attribute(ATTR_SKIP_END)
    GenericAttributeValue<String> getSkipEnd();
    @NotNull
    @Attribute(ATTR_DYNAMIC)
    GenericAttributeValue<String> getDynamic();
    @NotNull
    @Attribute(ATTR_BACKGROUND_SUBMIT)
    GenericAttributeValue<String> getBackgroundSubmit();
    @NotNull
    @Attribute(ATTR_BACKGROUND_RELOAD_ID)
    GenericAttributeValue<String> getBackgroundReloadId();
    @NotNull
    @Attribute(ATTR_BACKGROUND_HIDE_ID)
    GenericAttributeValue<String> getBackgroundHideId();
    @NotNull
    @Attribute(ATTR_BACKGROUND_MESSAGE)
    GenericAttributeValue<String> getBackgroundMessage();
    @NotNull
    @Attribute(ATTR_SERVER_STATIC)
    GenericAttributeValue<String> getServerStatic();
    @NotNull
    @Attribute(ATTR_BODY_PARAMETERS)
    GenericAttributeValue<String> getBodyParameters();
    @NotNull
    @Attribute(ATTR_PASS_THROUGH_PARAMETERS)
    GenericAttributeValue<String> getPassThroughParameters();
    @NotNull
    @Attribute(ATTR_EXCLUDE_EMPTY_FIELDS)
    GenericAttributeValue<String> getExcludeEmptyFields();

    @NotNull
    @SubTagList(AutoFieldsService.TAG_NAME)
    List<AutoFieldsService> getAutoFieldsServiceList();
    @NotNull
    @SubTagList(AutoFieldsEntity.TAG_NAME)
    List<AutoFieldsEntity> getAutoFieldsEntityList();
//    @NotNull
//    @SubTagList(Field.TAG_NAME)
//    List<Field> getFieldList();

    @NotNull
    @SubTag(FieldLayout.TAG_NAME)
    FieldLayout getFieldLayout();

}
