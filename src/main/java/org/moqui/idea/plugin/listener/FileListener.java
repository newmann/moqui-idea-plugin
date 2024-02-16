package org.moqui.idea.plugin.listener;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.sun.jna.platform.FileMonitor;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.CustomNotifier;

import java.util.List;

public class FileListener implements BulkFileListener {
    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        BulkFileListener.super.after(events);
        for(VFileEvent event: events) {
            System.out.println(event.toString());
        }

    }
}
