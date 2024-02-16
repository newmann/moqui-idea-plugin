package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.ServiceCallConverter;
import org.moqui.idea.plugin.dom.presentation.SecaPresentationProvider;

@Presentation(icon = "MoquiIcons.SecaTag", provider = SecaPresentationProvider.class)
public interface Seca extends DomElement {
    public static final String TAG_NAME = "seca";
    public static final String ATTR_ID = "id";
    public static final String ATTR_SERVICE = "service";
    public static final String ATTR_WHEN = "when";
    public static final String ATTR_NAME_IS_PATTERN = "name-is-pattern";
    public static final String ATTR_RUN_ON_ERROR = "run_on_error";
    public static final String ATTR_PRIORITY = "priority";
    @NotNull
    @Attribute(ATTR_ID)
    GenericAttributeValue<String> getId();

    @NotNull
    @Attribute(ATTR_SERVICE)
    @Convert(ServiceCallConverter.class)
    GenericAttributeValue<String> getService();

    @NotNull
    @Attribute(ATTR_WHEN)
    GenericAttributeValue<String> getWhen();
    @NotNull
    @Attribute(ATTR_NAME_IS_PATTERN)
    GenericAttributeValue<Boolean> getNameIsPattern();
    @NotNull
    @Attribute(ATTR_RUN_ON_ERROR)
    GenericAttributeValue<Boolean> getRunOnError();

    @NotNull
    @Attribute(ATTR_PRIORITY)
    GenericAttributeValue<Integer> getPriority();

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
