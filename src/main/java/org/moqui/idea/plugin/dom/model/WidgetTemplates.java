package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.Stubbed;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
//@Stubbed
public interface WidgetTemplates extends DomElement {
    public static final String TAG_NAME = "widget-templates";

    @NotNull
    @SubTagList(WidgetTemplate.TAG_NAME)
    List<WidgetTemplate> getWidgetTemplateList();


}
