package org.moqui.idea.plugin.action.componentManagement;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
@Deprecated
public class ComponentDependsAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project != null) {
            ComponentDepends componentDepends = new ComponentDepends(project);
            //todo
//            componentDepends.setVisible(true);
        }
    }
}
