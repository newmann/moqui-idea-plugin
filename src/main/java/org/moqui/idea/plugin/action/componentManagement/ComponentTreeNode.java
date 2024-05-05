package org.moqui.idea.plugin.action.componentManagement;

import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Component;
import org.moqui.idea.plugin.util.MyDomUtils;

public class ComponentTreeNode {
    private final Component component;
    private final String name;

    private final String version;


    ComponentTreeNode(@NotNull Component component) {
        this.component = component;
        this.name = MyDomUtils.getValueOrEmptyString(component.getName());
        this.version = MyDomUtils.getXmlAttributeValueString(component.getVersion()).orElse("N/A");
    }

    public String toString(){
        return this.name +" : " + this.version;
    }

    public Component getComponent(){
        return this.component;
    }

}
