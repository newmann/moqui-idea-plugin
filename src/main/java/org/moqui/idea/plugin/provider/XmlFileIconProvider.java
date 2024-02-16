package org.moqui.idea.plugin.provider;

import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.icon.MyIcons;
import org.moqui.idea.plugin.util.MyDomUtils;
import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

public class XmlFileIconProvider extends IconProvider {

    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement element, int flags) {
        final Optional<String> rootTagName = MyDomUtils.getRootTagName(element.getContainingFile());

        if (rootTagName.isEmpty()) return null;

        switch (rootTagName.get()) {
            case Services.TAG_NAME:
                return MyIcons.FILE_ICON_SERVICES;
            case Entities.TAG_NAME:
                return MyIcons.FILE_ICON_ENTITIES;
            case Secas.TAG_NAME:
                return MyIcons.FILE_ICON_SECAS;
            case Eecas.TAG_NAME:
                return MyIcons.FILE_ICON_EECAS;
            case Screen.TAG_NAME:
                return MyIcons.FILE_ICON_SCREEN;
            case MoquiConf.TAG_NAME:
                return MyIcons.FILE_ICON_MOQUI_CONF;
            case Resource.TAG_NAME:
                return MyIcons.FILE_ICON_REST_API;
            case Emecas.TAG_NAME:
                return MyIcons.FILE_ICON_EMECAS;
            case Component.TAG_NAME:
                return MyIcons.FILE_ICON_COMPONENT;
            default:
                return null;
        }

    }
}
