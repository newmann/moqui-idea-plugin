package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.presentation.TransitionPresentationProvider;

import java.util.List;
@Presentation(icon ="MoquiIcons.TransitionTag", provider = TransitionPresentationProvider.class)
public interface Transition extends TransitionAbstract {
    public static final String TAG_NAME = "transition";


//    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getMethod();
    @NotNull GenericAttributeValue<String> getBeginTransaction();
    @NotNull GenericAttributeValue<String> getReadOnly();
    @NotNull GenericAttributeValue<String> getRequireSessionToken();

//
//    @NotNull
//    @Attribute("xsi:noNamespaceSchemaLocation")
//    GenericAttributeValue<String> getXsiNoNamespaceSchemaLocation();

    @NotNull
    @SubTag(ServiceCall.TAG_NAME)
    ServiceCall getServiceCall();

    @NotNull
    @SubTagList(Parameter.TAG_NAME)
    List<Parameter> getParameterList();

    @NotNull
    @SubTagList(PathParameter.TAG_NAME)
    List<PathParameter> getPathParameterList();
    @NotNull
    @SubTag(Condition.TAG_NAME)
    Condition getCondition();

    @NotNull
    @SubTag(Actions.TAG_NAME)
    Actions getActions();

    @NotNull
    @SubTagList(ConditionalResponse.TAG_NAME)
    List<ConditionalResponse> getConditionalResponseList();
    @NotNull
    @SubTag(DefaultResponse.TAG_NAME)
    DefaultResponse getDefaultResponse();

    @NotNull
    @SubTag(ErrorResponse.TAG_NAME)
    ErrorResponse getErrorResponse();


}
