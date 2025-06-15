package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.Component;
import org.moqui.idea.plugin.util.ComponentUtils;

import javax.swing.*;

public class ComponentDescription extends DomFileDescription<Component> {

    public ComponentDescription(){
        super(Component.class,Component.TAG_NAME);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return ComponentUtils.isComponentFile(file);
    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
        return MyIcons.ComponentTag; //MyIcons.FILE_ICON_COMPONENT;
//        return super.getFileIcon(flags);
    }
//    public EntitiesDescription(Class<Entities> rootElementClass,  String rootTagName,  String @NotNull ... allPossibleRootTagNamespaces) {
//        super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);

//    }
//    @Override
//    protected void initializeFileDescription() {
//        registerNamespacePolicy("EntitiesXml", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
//    }


}
