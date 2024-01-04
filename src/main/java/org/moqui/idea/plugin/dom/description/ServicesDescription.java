package org.moqui.idea.plugin.dom.description;

import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.util.ServiceUtils;
import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServicesDescription extends DomFileDescription<Services> {

    public ServicesDescription(){
        super(Services.class,Services.TAG_NAME);
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
        return ServiceUtils.isServicesFile(file);
    }
}
