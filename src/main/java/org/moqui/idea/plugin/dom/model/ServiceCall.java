package org.moqui.idea.plugin.dom.model;

import com.intellij.ide.presentation.Presentation;
import com.intellij.util.xml.*;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.ServiceCallInMapReferenceConverter;
import org.moqui.idea.plugin.dom.converter.ServiceCallReferenceConverter;
import org.moqui.idea.plugin.dom.presentation.ServiceCallPresentationProvider;

import java.util.List;
@Presentation(icon = "org.moqui.idea.plugin.MyIcons.ServiceCallTag",provider = ServiceCallPresentationProvider.class)
public interface ServiceCall extends DomElement {
    
    String TAG_NAME = "service-call";
    String ATTR_NAME = "name";
    
    String ATTR_IN_MAP = "in-map";
    
    String ATTR_OUT_MAP = "out-map";
    
    String ATTR_OUT_MAP_ADD_TO_EXISTING = "out-map-add-to-existing";
    @NotNull
    @Attribute(ATTR_NAME)
    @Referencing(ServiceCallReferenceConverter.class)
    GenericAttributeValue<String> getName();

    @NotNull
    @Attribute(ATTR_IN_MAP)
    @Referencing(ServiceCallInMapReferenceConverter.class)
    GenericAttributeValue<String> getInMap();

    @NotNull
    @Attribute(ATTR_OUT_MAP)
    GenericAttributeValue<String> getOutMap();
    @NotNull
    @Attribute(ATTR_OUT_MAP_ADD_TO_EXISTING)
    GenericAttributeValue<String> getOutMapAddToExisting();

    @NotNull
    GenericAttributeValue<String> getAsync();

    @NotNull
    GenericAttributeValue<Boolean> getIncludeUserLogin();

    @NotNull
    GenericAttributeValue<String> getTransaction();

    @NotNull
    GenericAttributeValue<String> getTransactionTimeout();
    @NotNull
    GenericAttributeValue<Boolean> getIgnoreError();
    @NotNull
    GenericAttributeValue<String> getWebSendJsonResponse();

    @NotNull
    GenericAttributeValue<Boolean> getDisableAuthz();

    @NotNull
    @SubTagList(FieldMap.TAG_NAME)
    List<FieldMap> getFieldMapList();

}
