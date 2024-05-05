package org.moqui.idea.plugin.action.componentManagement;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
@Deprecated
public class ComponentManagementToolWindowsFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ComponentDepends componentDepends = new ComponentDepends(project);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(componentDepends.getMainPanel(),"",false);
        toolWindow.getContentManager().addContent(content);
    }
}
