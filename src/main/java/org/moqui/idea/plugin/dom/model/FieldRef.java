package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.FieldRefConverter;
import org.moqui.idea.plugin.dom.presentation.FieldRefPresentationProvider;

@Presentation(provider = FieldRefPresentationProvider.class)
public interface FieldRef extends DomElement {

    public static final String TAG_NAME = "field-ref";

    @NotNull
    @Convert(FieldRefConverter.class)
    GenericAttributeValue<String> getName();
}
