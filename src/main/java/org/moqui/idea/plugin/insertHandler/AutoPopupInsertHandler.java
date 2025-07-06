package org.moqui.idea.plugin.insertHandler;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class AutoPopupInsertHandler implements InsertHandler<LookupElement> {
    public static AutoPopupInsertHandler of(){
        return new AutoPopupInsertHandler();
    }

    @Override
    public void handleInsert(@NotNull InsertionContext insertionContext, @NotNull LookupElement lookupElement) {
        Project project = insertionContext.getProject();
        Editor editor = insertionContext.getEditor();
        AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);

    }
}
