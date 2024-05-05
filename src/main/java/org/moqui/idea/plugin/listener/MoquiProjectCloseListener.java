package org.moqui.idea.plugin.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.MoquiIndexService;

public class MoquiProjectCloseListener implements ProjectManagerListener {
    @Override
    public void projectClosed(@NotNull Project project) {
        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
        moquiIndexService.unRegisterListener();

        ProjectManagerListener.super.projectClosed(project);
    }
}
