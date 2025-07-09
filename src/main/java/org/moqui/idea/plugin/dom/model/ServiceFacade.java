package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ServiceFacade extends DomElement {

    String TAG_NAME = "service-facade";

    @NotNull GenericAttributeValue<String> getDistributedFactory();
    @NotNull GenericAttributeValue<String> getScheduledJobCheckTime();
    @NotNull GenericAttributeValue<String> getJobQueueMax();
    @NotNull GenericAttributeValue<String> getJobPoolCore();
    @NotNull GenericAttributeValue<String> getJobPoolMax();
    @NotNull GenericAttributeValue<String> getJobPoolAlive();

    @NotNull
    @SubTagList(ServiceLocation.TAG_NAME)
    List<ServiceLocation> getServiceLocationList();
    @NotNull
    @SubTagList(ServiceType.TAG_NAME)
    List<ServiceType> getServiceTypeList();
    @NotNull
    @SubTagList(ServiceFile.TAG_NAME)
    List<ServiceFile> getServiceFileList();


}
