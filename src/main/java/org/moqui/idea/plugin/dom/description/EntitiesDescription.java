package org.moqui.idea.plugin.dom.description;

import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntitiesDescription  extends DomFileDescription<Entities> {

    public EntitiesDescription(){
        super(Entities.class,Entities.TAG_NAME);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return EntityUtils.isEntitiesFile(file);
    }

//    public EntitiesDescription(Class<Entities> rootElementClass, @NonNls String rootTagName, @NonNls String @NotNull ... allPossibleRootTagNamespaces) {
//        super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);

//    }
//    @Override
//    protected void initializeFileDescription() {
//        registerNamespacePolicy("xsi:noNamespaceSchemaLocation", "http://moqui.org/xsd/entity-definition-3.xsd");
//    }

}
