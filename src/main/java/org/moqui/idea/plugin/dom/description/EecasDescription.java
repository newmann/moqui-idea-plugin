package org.moqui.idea.plugin.dom.description;

import com.intellij.util.xml.highlighting.DomElementsAnnotator;
import icons.MoquiIcons;
import org.moqui.idea.plugin.annotator.MoquiDomAnnotator;
import org.moqui.idea.plugin.dom.model.Eecas;
import org.moqui.idea.plugin.icon.MyIcons;
import org.moqui.idea.plugin.util.EecaUtils;
import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EecasDescription extends DomFileDescription<Eecas> {

    public EecasDescription(){
        super(Eecas.class,Eecas.TAG_NAME);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return EecaUtils.isEecasFile(file);
    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
//        return super.getFileIcon(flags);
        return MoquiIcons.EecaTag; //MyIcons.FILE_ICON_EECAS;
    }
//    public EntitiesDescription(Class<Entities> rootElementClass, @NonNls String rootTagName, @NonNls String @NotNull ... allPossibleRootTagNamespaces) {
//        super(rootElementClass, rootTagName, allPossibleRootTagNamespaces);

//    }
//    @Override
//    protected void initializeFileDescription() {
//        registerNamespacePolicy("EntitiesXml", "http://mybatis.org/dtd/mybatis-3-mapper.dtd");
//    }

}
