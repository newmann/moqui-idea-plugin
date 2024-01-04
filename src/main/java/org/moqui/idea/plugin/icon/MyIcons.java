package org.moqui.idea.plugin.icon;

import com.intellij.ui.IconManager;

import javax.swing.*;

public class MyIcons {
    public static final IconManager iconManager = IconManager.getInstance();

    public static final Icon FILE_ICON_SERVICES = iconManager.getIcon("/icons/file-icon-services.svg", MyIcons.class);
    public static final Icon FILE_ICON_ENTITIES = iconManager.getIcon("/icons/file-icon-entities.svg", MyIcons.class);
    public static final Icon FILE_ICON_EECAS = iconManager.getIcon("/icons/file-icon-eecas.svg", MyIcons.class);
    public static final Icon FILE_ICON_SECAS = iconManager.getIcon("/icons/file-icon-secas.svg", MyIcons.class);

    public static final Icon NAVIGATE_TO_ENTITY = iconManager.getIcon("/icons/file-icon-entities.svg", MyIcons.class);

    public static final Icon NAVIGATE_TO_SERVICE = iconManager.getIcon("/icons/file-icon-services.svg", MyIcons.class);
}
