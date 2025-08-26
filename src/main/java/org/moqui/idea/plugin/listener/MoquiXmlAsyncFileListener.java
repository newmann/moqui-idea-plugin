package org.moqui.idea.plugin.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
@Deprecated
public class MoquiXmlAsyncFileListener implements AsyncFileListener {
    public static MoquiXmlAsyncFileListener of(@NotNull Project project){
        return new MoquiXmlAsyncFileListener(project);
    }
    private final Project project;
    public MoquiXmlAsyncFileListener(@NotNull Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public ChangeApplier prepareChange(@NotNull List<? extends VFileEvent> list) {
        final List<VFileContentChangeEvent> contentChanges = ContainerUtil.findAll(list, VFileContentChangeEvent.class);

        for(VFileEvent event: contentChanges) {
            VirtualFile file = event.getFile();
            MoquiXmlBulkFileListener.processUpdatedStamp(project,file);// 这里只处理内容变更事件
        }
        return null;
    }
}
