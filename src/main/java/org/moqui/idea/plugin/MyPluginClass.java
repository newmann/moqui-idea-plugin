package org.moqui.idea.plugin;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;


public class MyPluginClass extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);
        if(project == null) return;
        StringBuilder sb = new StringBuilder();
        String projectName = project.getName();
        sb.append("Project Name: ");
        sb.append(projectName);

        String messageStr ="The plugin has been installed, welcome to use";

        String title = "Moqui Idea Plugin";
        Messages.showMessageDialog(project,
                sb.toString(),
                title,
                Messages.getInformationIcon());

    }
}
