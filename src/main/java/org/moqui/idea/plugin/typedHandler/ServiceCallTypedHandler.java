package org.moqui.idea.plugin.typedHandler;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.ServiceCall;

public class ServiceCallTypedHandler extends TypedHandlerDelegate {
    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if (!(file instanceof XmlFile)) return Result.CONTINUE;
//        XmlFile xmlFile = (XmlFile) file;

        final PsiElement at = file.findElementAt(editor.getCaretModel().getOffset());
        if (at == null || !(at.getParent() instanceof XmlTag)) {
            return Result.CONTINUE;
        }
        XmlTag tag= (XmlTag) at.getParent();
        if (tag.getName().equals(ServiceCall.TAG_NAME)) {
            if(c == '.') {
                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
                return Result.STOP;
            }
        }
        return Result.CONTINUE;

    }
}
