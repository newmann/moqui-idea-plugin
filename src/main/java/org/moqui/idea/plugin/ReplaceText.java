package org.moqui.idea.plugin;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ReplaceText extends AnAction {
    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * Sets visibility and enables this action menu item if:
     * <ul>
     *   <li>a project is open</li>
     *   <li>an editor is active</li>
     *   <li>some characters are selected</li>
     * </ul>
     *
     * @param e Event related to this action
     */
    @Override
    public void update(@NotNull final AnActionEvent e) {
        // Get required data keys
        final Project project = e.getProject();
        final Editor editor = e.getData(CommonDataKeys.EDITOR);
        // Set visibility and enable only in case of existing project and editor and if a selection exists
        e.getPresentation().setEnabledAndVisible(
                project != null && editor != null && editor.getSelectionModel().hasSelection()
        );
    }

    /**
     * Replaces the run of text selected by the primary caret with a fixed string.
     *
     * @param e Event related to this action
     */
    @Override
    public void actionPerformed(@NotNull final AnActionEvent e) {
        // Get all the required data from data keys
        // Editor and Project were verified in update(), so they are not null.
        final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();
        // Work off of the primary caret to get the selection info
        Caret primaryCaret = editor.getCaretModel().getPrimaryCaret();
        int start = primaryCaret.getSelectionStart();
        int end = primaryCaret.getSelectionEnd();
        // Replace the selection with a fixed string.
        // Must do this document change in a write action context.
        WriteCommandAction.runWriteCommandAction(project, () ->
                document.replaceString(start, end, "Replacement")
        );
        // De-select the text range that was just replaced
        primaryCaret.removeSelection();
    }
}
