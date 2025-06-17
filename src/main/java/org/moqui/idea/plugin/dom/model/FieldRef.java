package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.FieldRefReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.FieldRefPresentationProvider;

@Presentation(provider = FieldRefPresentationProvider.class)
public interface FieldRef extends DomElement {

    String TAG_NAME = "field-ref";
    String ATTR_NAME ="name";

    @NotNull
    @Attribute(ATTR_NAME)
    @Referencing(FieldRefReferenceConverter.class)
    GenericAttributeValue<String> getName();
}
