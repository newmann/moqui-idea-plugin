package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.ScreenExtend;
import org.moqui.idea.plugin.util.ScreenExtendUtils;

import javax.swing.*;

public class ScreenExtendDescription extends DomFileDescription<ScreenExtend> {

    public ScreenExtendDescription(){
        super(ScreenExtend.class, ScreenExtend.TAG_NAME);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return ScreenExtendUtils.isScreenExtendFile(file);
    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
//        return super.getFileIcon(flags);
        return MyIcons.ScreenTag;//MyIcons.FILE_ICON_SCREEN;
    }
//    public EntitiesDescription(Class<Entities> rootElementClass,  String rootTagName, String @NotNull ... allPossibleRootTagNamespaces) {
//        super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);

//    }
//    @Override
//    protected void initializeFileDescription() {
//        registerNamespacePolicy("EntitiesXml", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
//    }



}
