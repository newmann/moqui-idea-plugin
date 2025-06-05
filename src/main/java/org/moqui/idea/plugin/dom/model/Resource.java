package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.ResourcePresentationProvider;

import java.util.List;
//@Stubbed
@Presentation(provider = ResourcePresentationProvider.class)
public interface Resource extends DomElement {
    public static final String TAG_NAME = "resource";

    public static final String ATTR_NoNamespaceSchemaLocation = "xsi:noNamespaceSchemaLocation";
    public static final String VALUE_NoNamespaceSchemaLocation = "http://moqui.org/xsd/rest-api-3.xsd";
    public static final String ATTR_DISPLAY_NAME = "displayName";

    @NotNull
    @SubTag(Id.TAG_NAME)
    Id getId();
    @NotNull
    @SubTagList(Method.TAG_NAME)
    List<Method> getMethodList();
    @NotNull
    @SubTagList(Resource.TAG_NAME)
    List<Resource> getResourceList();

    @NotNull
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute(ATTR_DISPLAY_NAME)
    GenericAttributeValue<String> getDisplayName();

    @NotNull
    GenericAttributeValue<String> getDescription();
    @NotNull
    GenericAttributeValue<String> getVersion();

    @NotNull
    GenericAttributeValue<String> getRequireAuthentication();

}
