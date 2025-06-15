package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FormListColumn extends DomElement {

    public static final String TAG_NAME = "form-list-column";

    @NotNull
    @SubTagList(FieldRef.TAG_NAME)
    List<FieldRef> getFieldRefList();
}
