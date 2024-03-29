package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.MoquiConf;
import org.moqui.idea.plugin.util.MoquiConfUtils;

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
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return MoquiConfUtils.isConfFile(file);
    }
}
