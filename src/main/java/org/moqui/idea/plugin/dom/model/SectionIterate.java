package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.presentation.SectionIteratePresentationProvider;

@Presentation(provider = SectionIteratePresentationProvider.class)
public interface SectionIterate extends Section {

    String TAG_NAME = "section-iterate";
//    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getList();
    @NotNull GenericAttributeValue<String> getEntry();
    @NotNull GenericAttributeValue<String> getKey();

//    String ATTR_CONDITION = "condition";
//    @NotNull
//    @Attribute(ATTR_CONDITION)
//    GenericAttributeValue<String> getConditionAttr();

    @NotNull GenericAttributeValue<Boolean> getPaginate();

}
