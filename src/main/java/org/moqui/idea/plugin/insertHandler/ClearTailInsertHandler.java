package org.moqui.idea.plugin.insertHandler;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import org.jetbrains.annotations.NotNull;

public class ClearTailInsertHandler implements InsertHandler<LookupElement> {
    public static ClearTailInsertHandler of(){
        return new ClearTailInsertHandler();
    }
    @Override
    public void handleInsert(@NotNull InsertionContext insertionContext, @NotNull LookupElement lookupElement) {
        Editor editor = insertionContext.getEditor();

        int tailOffset = insertionContext.getTailOffset();
        Document document = editor.getDocument();
        CharSequence charSequence = document.getImmutableCharSequence();
        int quoteIndex = findNextQuote(charSequence,tailOffset);
        document.deleteString(tailOffset,quoteIndex);
//        ApplicationManager.getApplication().invokeLater(() -> {
            // 将光标移动到字符"之后
            editor.getCaretModel().moveToOffset(tailOffset+1);
            editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
//        });

    }
    private int findNextQuote(CharSequence text, int start) {
        for (int i = start; i < text.length(); i++) {
            if (text.charAt(i) == '"') return i;
        }
        return -1;
    }
}
