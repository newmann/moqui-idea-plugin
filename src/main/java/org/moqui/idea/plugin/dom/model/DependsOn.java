package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.DependsOnPresentationProvider;

@Presentation(provider = DependsOnPresentationProvider.class)
public interface DependsOn extends DomElement {

    String TAG_NAME = "depends-on";


    @NotNull GenericAttributeValue<String> getField();
    @NotNull GenericAttributeValue<String> getParameter();

    //for moqui-conf-3.xsd
    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getVersion();

}
