package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Services extends DomElement {
    public static final String TAG_NAME = "services";
    @NotNull
    @SubTagList(Service.TAG_NAME)
    List<Service> getServices();
}
