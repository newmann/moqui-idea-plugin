package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.IdPresentationProvider;

import java.util.List;
@Presentation(provider = IdPresentationProvider.class)
public interface Id extends DomElement {
    public static final String TAG_NAME = "id";

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
    GenericAttributeValue<String> getRequireAuthentication();
    @NotNull
    GenericAttributeValue<String> getAllowExtraPath();

}
