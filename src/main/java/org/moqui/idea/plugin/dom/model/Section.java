package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.SecaPresentationProvider;
import org.moqui.idea.plugin.dom.presentation.SectionPresentationProvider;

@Presentation(provider = SectionPresentationProvider.class)
public interface Section extends SectionElements {
    public static final String TAG_NAME = "section";

    public static final String ATTR_CONDITION = "condition";

    @NotNull
    @NameValue
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute(ATTR_CONDITION)
    GenericAttributeValue<String> getConditionAttr();

}
