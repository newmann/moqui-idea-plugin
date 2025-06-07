package org.moqui.idea.plugin.listener;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.service.MoquiIndexService;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

import java.util.List;

public class MoquiXmlBulkFileListener implements BulkFileListener {
    private static Logger LOGGER = Logger.getInstance(MoquiXmlBulkFileListener.class);
    public static MoquiXmlBulkFileListener of(@NotNull Project project){
        return new MoquiXmlBulkFileListener(project);
    }

    private final Project project;
    public MoquiXmlBulkFileListener(@NotNull Project project){
        this.project = project;
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        BulkFileListener.super.after(events);
        for(VFileEvent event: events) {

            VirtualFile file = event.getFile();
            if(file == null || (!file.isValid())) continue;

//            LOGGER.warn(file.getName()+" has been updated.");

            PsiManager psiManager = PsiManager.getInstance(project);
            PsiFile psiFile = psiManager.findFile(file);

            if(psiFile != null) {
                switch (MyDomUtils.getRootTagName(psiFile).orElse(MyStringUtils.EMPTY_STRING)){
                    case Services.TAG_NAME -> project.getService(MoquiIndexService.class).setServiceXmlFileLastUpdatedStamp(System.currentTimeMillis());
                    case Entities.TAG_NAME -> {
                        MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);
                        moquiIndexService.setEntityXmlFileLastUpdatedStamp(System.currentTimeMillis());
                        moquiIndexService.setServiceXmlFileLastUpdatedStamp(System.currentTimeMillis());//修改entity的xml，则service也需要更新
                    }
                    case EntityFacadeXml.TAG_NAME -> {

                    }
                }

            }

        }
    }
}
