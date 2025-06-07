package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.moqui.idea.plugin.MyIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Resource;
import org.moqui.idea.plugin.util.RestApiUtils;

import javax.swing.*;

public class RestApiDescription extends DomFileDescription<Resource> {

    public RestApiDescription(){
        super(Resource.class, Resource.TAG_NAME);
    }
//    public EntitiesDescription(Class<Entities> rootElementClass,  String rootTagName,  String @NotNull ... allPossibleRootTagNamespaces) {
//        super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);
//    }
//    @Override
//    protected void initializeFileDescription() {
//        registerNamespacePolicy("EntitiesXml", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
//    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
//        return super.getFileIcon(flags);
        return MyIcons.ServiceTypeRemoteRest;//MyIcons.FILE_ICON_REST_API;
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return RestApiUtils.isRestApiFile(file);
    }

}
