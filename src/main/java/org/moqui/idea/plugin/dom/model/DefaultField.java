package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import org.jetbrains.annotations.NonNls;
import org.moqui.idea.plugin.dom.presentation.DefaultFieldPresentationProvider;

@Presentation(provider = DefaultFieldPresentationProvider.class)
public interface DefaultField extends FormListDefaultField {

    public static final String TAG_NAME = "default-field";

}
