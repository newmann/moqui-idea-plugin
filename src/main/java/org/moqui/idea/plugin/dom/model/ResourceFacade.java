package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;

import java.util.List;

public interface ResourceFacade extends DomElement {
    public static final String TAG_NAME = "resource-facade";



    @NotNull
    @Convert(LocationConverter.class)
    GenericAttributeValue<String> getXmlActionsTemplateLocation();

    @NotNull GenericAttributeValue<String> getXmlFoHandlerFactory();

    @NotNull
    @SubTagList(ResourceReference.TAG_NAME)
    List<ResourceReference> getResourceReferenceList();

    @NotNull
    @SubTagList(TemplateRenderer.TAG_NAME)
    List<TemplateRenderer> getTemplateRendererList();
    @NotNull
    @SubTagList(ScriptRunner.TAG_NAME)
    List<ScriptRunner> getScriptRunnerList();

}
