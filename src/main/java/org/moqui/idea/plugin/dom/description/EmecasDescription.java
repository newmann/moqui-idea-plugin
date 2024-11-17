package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Emecas;
import org.moqui.idea.plugin.util.EmecaUtils;

import javax.swing.*;

public class EmecasDescription extends DomFileDescription<Emecas> {

    public EmecasDescription(){
        super(Emecas.class,Emecas.TAG_NAME);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return EmecaUtils.isEmecasFile(file);
    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
//        return super.getFileIcon(flags);
        return MoquiIcons.EmecasTag; //MyIcons.FILE_ICON_EMECAS;
    }

    //    public EntitiesDescription(Class<Entities> rootElementClass, @NonNls String rootTagName, @NonNls String @NotNull ... allPossibleRootTagNamespaces) {
//        super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);

//    }
//    @Override
//    protected void initializeFileDescription() {
//        registerNamespacePolicy("EntitiesXml", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
//    }


}
