package org.moqui.idea.plugin.provider;

import org.moqui.idea.plugin.dom.model.Eecas;
import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.dom.model.Secas;
import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.icon.MyIcons;
import org.moqui.idea.plugin.util.DomUtils;
import com.intellij.ide.IconProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;

public class XmlFileIconProvider extends IconProvider {

    @Override
    public @Nullable Icon getIcon(@NotNull PsiElement element, int flags) {
        final Optional<String> rootTagName = DomUtils.getRootTagName(element.getContainingFile());

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
            default:
                return null;
        }

    }
}
