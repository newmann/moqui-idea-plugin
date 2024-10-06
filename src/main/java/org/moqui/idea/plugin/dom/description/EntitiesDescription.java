package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import com.intellij.util.xml.highlighting.DomElementsAnnotator;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.annotator.MoquiDomAnnotator;
import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.icon.MyIcons;
import org.moqui.idea.plugin.util.EntityUtils;

import javax.swing.*;

public class EntitiesDescription  extends DomFileDescription<Entities> {

    public EntitiesDescription(){
        super(Entities.class,Entities.TAG_NAME);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {

//        return EntityUtils.isEntitiesFile(file) && super.isMyFile(file, module);
        return EntityUtils.isEntitiesFile(file);
    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
//        return super.getFileIcon(flags);
        return MoquiIcons.EntityTag; //MyIcons.FILE_ICON_ENTITIES;
    }

//    public EntitiesDescription(Class<Entities> rootElementClass, @NonNls String rootTagName, @NonNls String @NotNull ... allPossibleRootTagNamespaces) {
//        super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);

//    }
//    @Override
//    protected void initializeFileDescription() {
//        registerNamespacePolicy("xsi:noNamespaceSchemaLocation", "http://moqui.org/xsd/entity-definition-3.xsd");
//    }


//    @Override
//    protected void initializeFileDescription() {
//        super.initializeFileDescription();
//        registerReferenceInjector(new EntityPSiReferenceInjector());
//    }
}
