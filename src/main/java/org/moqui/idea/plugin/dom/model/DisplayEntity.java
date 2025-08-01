package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.Referencing;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.EntityAndViewNameReferenceConverter;
import org.moqui.idea.plugin.dom.converter.TextTemplateConverter;

public interface DisplayEntity extends DomElement {

    String TAG_NAME ="display-entity";

//    @NotNull
//    @SubTagList(DependsOn.TAG_NAME)
//    List<DependsOn> getDependsOnList();
    @NotNull
//    @Convert(EntityFullNameConverter.class)
    @Referencing(EntityAndViewNameReferenceConverter.class)
    GenericAttributeValue<String> getEntityName();

    @NotNull GenericAttributeValue<String> getKeyFieldName();
    @NotNull GenericAttributeValue<String> getUseCache();
    @NotNull
    @Convert(TextTemplateConverter.class)
    GenericAttributeValue<String> getText();
    @NotNull GenericAttributeValue<String> getDefaultText();
    @NotNull GenericAttributeValue<String> getStyle();
    @NotNull GenericAttributeValue<Boolean> getAlsoHidden();
    @NotNull GenericAttributeValue<String> getDisplayType();
    @NotNull GenericAttributeValue<String> getEncode();

    @NotNull GenericAttributeValue<String> getTextMap();

}
