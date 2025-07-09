package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NotNull;

public interface File extends DomElement {
    String TAG_NAME ="file";

//    @NotNull
//    @SubTagList(DependsOn.TAG_NAME)
//    List<DependsOn> getDependsOnList();
    @NotNull GenericAttributeValue<Boolean> getSize();
    @NotNull GenericAttributeValue<Boolean> getMaxlength();
    @NotNull GenericAttributeValue<String> getDefaultValue();
    @NotNull GenericAttributeValue<Boolean> getMultiple();
    @NotNull GenericAttributeValue<String> getAccept();


}
