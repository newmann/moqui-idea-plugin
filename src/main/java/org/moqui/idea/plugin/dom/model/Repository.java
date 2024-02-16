package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Repository extends DomElement {
    public static final String TAG_NAME = "repository";

    @NotNull GenericAttributeValue<String> getName();
    @NotNull GenericAttributeValue<String> getWorkspace();
    @NotNull GenericAttributeValue<String> getUsername();
    @NotNull GenericAttributeValue<String> getPassword();

    @NotNull
    @SubTagList(InitParam.TAG_NAME)
    List<InitParam> getInitParamList();

}
