package org.moqui.idea.plugin.listener;

import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FileListener implements BulkFileListener {
    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        BulkFileListener.super.after(events);
        for(VFileEvent event: events) {
            System.out.println(event.toString());
        }

    }
}
