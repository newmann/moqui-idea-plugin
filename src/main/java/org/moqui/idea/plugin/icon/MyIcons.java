package org.moqui.idea.plugin.icon;

import com.intellij.ui.IconManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public final class MyIcons {
    public static final IconManager iconManager = IconManager.getInstance();
    private static @NotNull Icon load(@NotNull String path, int cacheKey, int flags) {
        return IconManager.getInstance().loadRasterizedIcon(path, MyIcons.class.getClassLoader(), cacheKey, flags);
    }
    public static final Icon FILE_ICON_SERVICES = iconManager.getIcon("/icons/file-icon-services.svg", MyIcons.class);
    public static final Icon FILE_ICON_ENTITIES = iconManager.getIcon("/icons/file-icon-entities.svg", MyIcons.class);
    public static final Icon FILE_ICON_EECAS = iconManager.getIcon("/icons/file-icon-eecas.svg", MyIcons.class);
    public static final Icon FILE_ICON_SECAS = iconManager.getIcon("/icons/file-icon-secas.svg", MyIcons.class);
    public static final Icon FILE_ICON_VIEW = iconManager.getIcon("/icons/file-icon-view.svg", MyIcons.class);
    public static final Icon FILE_ICON_SCREEN = iconManager.getIcon("/icons/file-icon-screen.svg", MyIcons.class);

    public static final Icon FILE_ICON_MOQUI_CONF = iconManager.getIcon("/icons/file-icon-conf.svg", MyIcons.class);
    public static final Icon FILE_ICON_REST_API = iconManager.getIcon("/icons/file-icon-rest.svg", MyIcons.class);
    public static final Icon FILE_ICON_EMECAS = iconManager.getIcon("/icons/file-icon-email.svg", MyIcons.class);
    public static final Icon FILE_ICON_COMPONENT = iconManager.getIcon("/icons/file-icon-component.svg", MyIcons.class);
    public static final Icon NAVIGATE_TO_ENTITY = iconManager.getIcon("/icons/file-icon-entities.svg", MyIcons.class);

    public static final Icon NAVIGATE_TO_SERVICE = iconManager.getIcon("/icons/file-icon-services.svg", MyIcons.class);
    public static final Icon NAVIGATE_TO_VIEW = iconManager.getIcon("/icons/file-icon-view.svg", MyIcons.class);
    public static final Icon NAVIGATE_TO_SCREEN = iconManager.getIcon("/icons/file-icon-screen.svg", MyIcons.class);
}
