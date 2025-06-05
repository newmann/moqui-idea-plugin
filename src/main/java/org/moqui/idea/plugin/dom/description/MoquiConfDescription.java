package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.moqui.idea.plugin.MyIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.MoquiConf;
import org.moqui.idea.plugin.util.MoquiConfUtils;

import javax.swing.*;

public class MoquiConfDescription extends DomFileDescription<MoquiConf> {

    public MoquiConfDescription(){
        super(MoquiConf.class, MoquiConf.TAG_NAME);
    }
//    public EntitiesDescription(Class<Entities> rootElementClass, @NonNls String rootTagName, @NonNls String @NotNull ... allPossibleRootTagNamespaces) {
//        super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);
//    }
//    @Override
//    protected void initializeFileDescription() {
//        registerNamespacePolicy("EntitiesXml", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
//    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
//        return super.getFileIcon(flags);
        return MyIcons.MoquiConfTag; // MyIcons.FILE_ICON_MOQUI_CONF;
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return MoquiConfUtils.isMoquiConfFile(file);
    }
}
