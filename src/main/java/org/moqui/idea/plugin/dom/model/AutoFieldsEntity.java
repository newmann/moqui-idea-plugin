package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AutoFieldsEntity extends AbstractEntityName {
    
    String TAG_NAME = "auto-fields-entity";
    @NotNull
    @SubTagList(Exclude.TAG_NAME)
    List<Exclude> getExcludeList();

//    @NotNull GenericAttributeValue<String> getEntityName();
    @NotNull GenericAttributeValue<String> getFieldType();
    @NotNull GenericAttributeValue<String> getInclude();
    @NotNull GenericAttributeValue<Boolean> getAutoColumns();
}
