package org.moqui.idea.plugin.dom.description;

import com.intellij.openapi.module.Module;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.WidgetTemplates;
import org.moqui.idea.plugin.util.WidgetTemplateUtils;

import javax.swing.*;

public class WidgetTemplatesDescription extends DomFileDescription<WidgetTemplates> {

    public WidgetTemplatesDescription(){
        super(WidgetTemplates.class,WidgetTemplates.TAG_NAME);
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return WidgetTemplateUtils.isWidgetTemplateFile(file);
    }

    @Override
    public @Nullable Icon getFileIcon(int flags) {
        return MoquiIcons.WidgetTemplate; // MyIcons.FILE_ICON_WIDGET_TEMPLATES;
//        return super.getFileIcon(flags);
    }


}
