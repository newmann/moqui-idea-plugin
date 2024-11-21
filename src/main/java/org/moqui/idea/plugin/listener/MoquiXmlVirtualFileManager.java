package org.moqui.idea.plugin.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.service.MoquiIndexService;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

public class MoquiXmlVirtualFileManager implements VirtualFileListener {
    private Project project;
    public MoquiXmlVirtualFileManager(@NotNull Project project) {
        this.project = project;
    }


    @Override
    public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
        setUpdateFlag(event);

        VirtualFileListener.super.beforeFileDeletion(event);
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        setUpdateFlag(event);
        VirtualFileListener.super.contentsChanged(event);
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        setUpdateFlag(event);
        VirtualFileListener.super.fileCreated(event);
    }

    private void setUpdateFlag(@NotNull VirtualFileEvent event){
        VirtualFile file = event.getFile();
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile psiFile = psiManager.findFile(file);

        if(psiFile != null) {
            MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
            if(ServiceUtils.isServicesFile(psiFile)) {
                moquiIndexService.setServiceXmlFileLastUpdatedStamp(System.currentTimeMillis());
            }else {
                if(EntityUtils.isEntitiesFile(psiFile)) {
                    moquiIndexService.setEntityXmlFileLastUpdatedStamp(System.currentTimeMillis());
                    moquiIndexService.setServiceXmlFileLastUpdatedStamp(System.currentTimeMillis());//修改entity的xml，则service也需要更新
                }
            }
        }
    }
}
