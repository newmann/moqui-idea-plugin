package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Tools extends DomElement {
    
    public static final String TAG_NAME = "tools";

    @NotNull
    GenericAttributeValue<String> getEmptyDbLoad();

    @NotNull
    GenericAttributeValue<Integer> getWorkerQueue();
    @NotNull
    GenericAttributeValue<Integer> getWorkerPoolCore();
    @NotNull
    GenericAttributeValue<Integer> getWorkerPoolMax();
    @NotNull
    GenericAttributeValue<Integer> getWorkerPoolAlive();
    @NotNull
    GenericAttributeValue<String> getNotificationTopicFactory();


    @NotNull
    @SubTagList(ToolFactory.TAG_NAME)
    List<ToolFactory> getToolFactoryList();

}
