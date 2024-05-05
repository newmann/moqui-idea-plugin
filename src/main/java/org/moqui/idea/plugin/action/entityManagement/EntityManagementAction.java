package org.moqui.idea.plugin.action.entityManagement;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.action.componentManagement.ComponentDepends;
@Deprecated
public class EntityManagementAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if(project != null) {
            EntityManagementDialogWrapper dialogWrapper = new EntityManagementDialogWrapper(project);
            dialogWrapper.show();

        }
    }
}
