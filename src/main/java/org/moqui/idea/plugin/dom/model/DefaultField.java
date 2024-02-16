package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.DefaultFieldPresentationProvider;

@Presentation(provider = DefaultFieldPresentationProvider.class)
public interface DefaultField extends FormListDefaultField {
    public static final String TAG_NAME = "default-field";

}
