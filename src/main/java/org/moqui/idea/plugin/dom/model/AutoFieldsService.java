package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.ServiceCallReferenceConverter;

import java.util.List;

public interface AutoFieldsService extends DomElement {
    
    String TAG_NAME = "auto-fields-service";
    String ATTR_SERVICE_NAME = "service-name";


    @NotNull
    @SubTagList(Exclude.TAG_NAME)
    List<Exclude> getExcludeList();

    @NotNull
    @Attribute(ATTR_SERVICE_NAME)
    @Referencing(ServiceCallReferenceConverter.class)
    GenericAttributeValue<String> getServiceName();
    @NotNull GenericAttributeValue<String> getFieldType();
    @NotNull GenericAttributeValue<String> getInclude();

}
