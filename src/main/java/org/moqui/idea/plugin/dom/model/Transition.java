package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.TransitionPresentationProvider;

import java.util.List;
@Presentation(icon ="org.moqui.idea.plugin.MyIcons.TransitionTag", provider = TransitionPresentationProvider.class)
public interface Transition extends AbstractTransition {

    public static final String TAG_NAME = "transition";

    public static final String ATTR_READ_ONLY = "read-only";

    public static final String ATTR_BEGIN_TRANSACTION = "begin-transaction";

    public static final String ATTR_METHOD = "method";

    public static final String ATTR_REGURIE_SESSION_TOKEN = "require-session-token";
//    @NotNull GenericAttributeValue<String> getName();
    @NotNull
    @Attribute(ATTR_METHOD)
    GenericAttributeValue<String> getMethod();
    @NotNull
    @Attribute(ATTR_BEGIN_TRANSACTION)
    GenericAttributeValue<String> getBeginTransaction();

    @NotNull
    @Attribute(ATTR_READ_ONLY)
    GenericAttributeValue<String> getReadOnly();

    @NotNull
    @Attribute(ATTR_REGURIE_SESSION_TOKEN)
    GenericAttributeValue<String> getRequireSessionToken();


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
