package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.SecaPresentationProvider;
import org.moqui.idea.plugin.dom.presentation.SectionPresentationProvider;

@Presentation(provider = SectionPresentationProvider.class)
public interface Section extends SectionElements {
    public static final String TAG_NAME = "section";

    public static final String ATTR_CONDITION = "condition";

    @NotNull GenericAttributeValue<String> getName();
    @NotNull
    @Attribute(ATTR_CONDITION)
    GenericAttributeValue<String> getConditionAttr();

}
