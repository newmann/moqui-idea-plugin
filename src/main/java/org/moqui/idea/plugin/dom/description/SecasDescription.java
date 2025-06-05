package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.moqui.idea.plugin.MyIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Secas;
import org.moqui.idea.plugin.util.SecaUtils;

import javax.swing.*;

public class SecasDescription extends DomFileDescription<Secas> {

    public SecasDescription(){
        super(Secas.class,Secas.TAG_NAME);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return SecaUtils.isSecasFile(file);
    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
//        return super.getFileIcon(flags);
        return MyIcons.SecaTag; //MyIcons.FILE_ICON_SECAS;
    }
//    public EntitiesDescription(Class<Entities> rootElementClass, @NonNls String rootTagName, @NonNls String @NotNull ... allPossibleRootTagNamespaces) {
//        super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);

//    }
//    @Override
//    protected void initializeFileDescription() {
//        registerNamespacePolicy("EntitiesXml", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
//    }



}
