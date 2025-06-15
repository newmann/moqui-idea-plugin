package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface AutoFieldsService extends DomElement {
    
    public static final String TAG_NAME = "auto-fields-service";


    @NotNull
    @SubTagList(Exclude.TAG_NAME)
    List<Exclude> getExcludeList();

    @NotNull GenericAttributeValue<String> getServiceName();
    @NotNull GenericAttributeValue<String> getFieldType();
    @NotNull GenericAttributeValue<String> getInclude();

}
