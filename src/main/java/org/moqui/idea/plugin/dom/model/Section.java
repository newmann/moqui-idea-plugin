package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.SectionPresentationProvider;

@Presentation(provider = SectionPresentationProvider.class)
public interface Section extends SectionElements {
    
    public static final String TAG_NAME = "section";

    public static final String ATTR_CONDITION = "condition";
    public static final String ATTR_NAME = "name";
    @NotNull
    @NameValue
    @Attribute(ATTR_NAME)
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute(ATTR_CONDITION)
    GenericAttributeValue<String> getConditionAttr();

}
