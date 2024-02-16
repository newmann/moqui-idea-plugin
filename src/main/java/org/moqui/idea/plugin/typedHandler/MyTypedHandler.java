package org.moqui.idea.plugin.typedHandler;

import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class MyTypedHandler extends TypedHandlerDelegate {

    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        // Get the document and project
        final Document document = editor.getDocument();
        // Construct the runnable to substitute the string at offset 0 in the document
        Runnable runnable = () -> document.insertString(0, "editor_basics\n");
        // Make the document change in the context of a write action.
        WriteCommandAction.runWriteCommandAction(project, runnable);
        return Result.STOP;
    }
}
