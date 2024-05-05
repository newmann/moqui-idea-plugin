package org.moqui.idea.plugin.provider;

import icons.MoquiIcons;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.icon.MyIcons;
import org.moqui.idea.plugin.util.MyDomUtils;
import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;
@Deprecated
public class XmlFileIconProvider extends IconProvider {
//转移到各个Description中去
    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement element, int flags) {
        final Optional<String> rootTagName = MyDomUtils.getRootTagName(element.getContainingFile());

        if (rootTagName.isEmpty()) return null;

        switch (rootTagName.get()) {
            case Services.TAG_NAME:
                return MoquiIcons.ServiceTag;
            case Entities.TAG_NAME:
                return MoquiIcons.EntityTag;//MyIcons.FILE_ICON_ENTITIES;
            case Secas.TAG_NAME:
                return MoquiIcons.SecaTag;//MyIcons.FILE_ICON_SECAS;
            case Eecas.TAG_NAME:
                return MoquiIcons.EecaTag;//MyIcons.FILE_ICON_EECAS;
            case Screen.TAG_NAME:
                return MoquiIcons.ScreenTag; //MyIcons.FILE_ICON_SCREEN;
            case MoquiConf.TAG_NAME:
                return MoquiIcons.MoquiConfTag; //MyIcons.FILE_ICON_MOQUI_CONF;
            case Resource.TAG_NAME:
                return MoquiIcons.ServiceTypeRemoteRest;//MyIcons.FILE_ICON_REST_API;
            case Emecas.TAG_NAME:
                return MoquiIcons.EmecasTag;//MyIcons.FILE_ICON_EMECAS;
            case Component.TAG_NAME:
                return MoquiIcons.ComponentTag;//MyIcons.FILE_ICON_COMPONENT;
            case WidgetTemplates.TAG_NAME:
                return MoquiIcons.WidgetTemplate;//MyIcons.FILE_ICON_WIDGET_TEMPLATES;
            default:
                return null;
        }

    }
}
