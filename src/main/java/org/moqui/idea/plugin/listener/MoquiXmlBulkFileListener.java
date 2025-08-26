package org.moqui.idea.plugin.listener;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Entities;
import org.moqui.idea.plugin.dom.model.EntityFacadeXml;
import org.moqui.idea.plugin.dom.model.MoquiConf;
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
    public void before(@NotNull List<? extends VFileEvent> events) {
        BulkFileListener.super.before(events);
        // 在删除事件发生前，文件还是有效的，可以安全获取其类型
        final List<VFileDeleteEvent> deleteEvents = ContainerUtil.findAll(events, VFileDeleteEvent.class);
        for (VFileEvent event : deleteEvents) {
            VirtualFile file = event.getFile();
            // 此时文件仍然有效，可以获取类型，所以在这里处理
            processUpdatedStamp(project,file);
        }
    // VFileContentChangeEvent内部的变化处理
        final List<VFileContentChangeEvent> contentChanges = ContainerUtil.findAll(events, VFileContentChangeEvent.class);
        for(VFileEvent event: contentChanges) {
            VirtualFile file = event.getFile();
            processUpdatedStamp(project,file);// 这里只处理内容变更事件
        }
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        BulkFileListener.super.after(events);
// 在VFileCreateEvent和VFileMoveEvent可以安全地读取新文件内容，需要在这里处理
// VFileDeleteEvent文件已从 VFS 中移除，这里无法获取，需要在before过程中处理
// VFileContentChangeEvent内部的变化处理已经在before中处理了，这里不需要处理

        final List<VFileCreateEvent> createEvents = ContainerUtil.findAll(events, VFileCreateEvent.class);
        for(VFileEvent event: createEvents) {
            VirtualFile file = event.getFile();
            processUpdatedStamp(project,file);

        }
        final List<VFileMoveEvent> moveEvents = ContainerUtil.findAll(events, VFileMoveEvent.class);
        for(VFileEvent event: moveEvents) {
            VirtualFile file = event.getFile();
            processUpdatedStamp(project,file);

        }
    }
    public static void processUpdatedStamp(Project project, VirtualFile file){
        if(file == null || (!file.isValid())) return;

//            LOGGER.warn(file.getName()+" has been updated.");

        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile psiFile = psiManager.findFile(file);

        if(psiFile != null) {
            MoquiIndexService moquiIndexService = project.getService(MoquiIndexService.class);

            switch (MyDomUtils.getRootTagName(psiFile).orElse(MyStringUtils.EMPTY_STRING)){
                case Services.TAG_NAME -> moquiIndexService.setServiceXmlFileLastUpdatedStamp(System.currentTimeMillis());
                case Entities.TAG_NAME -> {

                    moquiIndexService.setEntityXmlFileLastUpdatedStamp(System.currentTimeMillis());
                    moquiIndexService.setServiceXmlFileLastUpdatedStamp(System.currentTimeMillis());//修改entity的xml，则service也需要更新
                }
                case EntityFacadeXml.TAG_NAME -> {
                    moquiIndexService.setEntityFacadeXmlFileLastUpdatedStamp(System.currentTimeMillis());
                }
                case MoquiConf.TAG_NAME ->{
                    moquiIndexService.setRootSubScreensItemXmlFileLastUpdatedStamp(System.currentTimeMillis());
                }
            }

        }

    }
}
