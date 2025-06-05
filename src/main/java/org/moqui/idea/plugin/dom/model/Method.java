package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.MethodPresentationProvider;
@Presentation(provider = MethodPresentationProvider.class)
public interface Method extends DomElement {

    public static final String TAG_NAME = "method";

    @NotNull
    @SubTag(Service.TAG_NAME)
    Service getService();
    @NotNull
    @SubTag(Entity.TAG_NAME)
    Entity getEntity();
    
    @NotNull
    GenericAttributeValue<String> getType();
    @NotNull
    GenericAttributeValue<String> getRequireAuthentication();

}
