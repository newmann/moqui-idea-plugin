package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.LocationConverter;
import org.moqui.idea.plugin.dom.converter.LocationReferenceConverter;

public interface ScreenTextOutput extends DomElement {

    String TAG_NAME = "screen-text-output";

    @NotNull GenericAttributeValue<String> getType();
    @NotNull GenericAttributeValue<String> getMimeType();
    @NotNull GenericAttributeValue<String> getAlwaysStandalone();
    @NotNull GenericAttributeValue<String> getSkipActions();
    @NotNull
//    @Convert(LocationConverter.class)
    @Referencing(LocationReferenceConverter.class)
    GenericAttributeValue<String> getMacroTemplateLocation();



}
