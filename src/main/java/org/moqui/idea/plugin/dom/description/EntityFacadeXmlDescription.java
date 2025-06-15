package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.util.EntityFacadeXmlUtils;

import javax.swing.*;

public class EntityFacadeXmlDescription extends DomFileDescription<EntityFacadeXml> {

    public EntityFacadeXmlDescription(){
        super(EntityFacadeXml.class,EntityFacadeXml.TAG_NAME);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {

        return EntityFacadeXmlUtils.isEntityFacadeXmlFile(file);
    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
        return MyIcons.EntityFacadeXmlTag;
    }
}
