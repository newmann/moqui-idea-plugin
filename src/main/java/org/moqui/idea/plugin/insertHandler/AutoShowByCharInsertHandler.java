package org.moqui.idea.plugin.insertHandler;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.sun.jna.WString;
import org.jetbrains.annotations.NotNull;

public class AutoShowByCharInsertHandler implements InsertHandler<LookupElement> {
    public static AutoShowByCharInsertHandler ofHash(){
        return new AutoShowByCharInsertHandler("#");
    }
    public static AutoShowByCharInsertHandler ofDot(){
        return new AutoShowByCharInsertHandler(".");
    }
    private final String signChar;
    AutoShowByCharInsertHandler(@NotNull String signChar){
        this.signChar = signChar;
    }
    @Override
    public void handleInsert(@NotNull InsertionContext insertionContext, @NotNull LookupElement lookupElement) {
        Project project = insertionContext.getProject();

        Editor editor = insertionContext.getEditor();

        int tailOffset = insertionContext.getTailOffset();
        Document document = editor.getDocument();
        document.insertString(tailOffset," ");
        document.insertString(tailOffset,signChar);

//        ApplicationManager.getApplication().invokeLater(() -> {
            // 将光标移动到字符"之后
            editor.getCaretModel().moveToOffset(tailOffset+1);
            editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
//        });

    }
}
