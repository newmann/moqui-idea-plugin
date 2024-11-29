package org.moqui.idea.plugin.typedHandler;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.contributor.MultiFieldNameCompletionContributor;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.provider.ServiceCallInMapCompletionProvider;
import org.moqui.idea.plugin.util.MyDomUtils;

public class ServiceCallTypedHandler extends TypedHandlerDelegate {
    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        if(!MyDomUtils.isMoquiXmlFile(file)) return Result.CONTINUE;
        final PsiElement at = file.findElementAt(editor.getCaretModel().getOffset()-1);
//        if (at == null || !(at.getParent() instanceof XmlTag)) {
        if (at == null) {
            return Result.CONTINUE;
        }
        switch (c){
            case ',','['->{
                if(ServiceCallInMapCompletionProvider.IN_MAP_PATTERN.accepts(at)){
                    AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
                    return Result.STOP;
                }

            }
            default -> {
                if(at.getParent() instanceof XmlTag tag) {
                    if (tag.getName().equals(ServiceCall.TAG_NAME)) {
                        if (c == '.') {
                            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
                            return Result.STOP;
                        }
                    }
                }

            }
        }
//        if (c == ',') {
//            if (MultiFieldNameCompletionContributor.getCapture().accepts(at)) {
//                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
//                return Result.STOP;
//            }
//        } else {
//            if(at.getParent() instanceof XmlTag tag) {
//                if (tag.getName().equals(ServiceCall.TAG_NAME)) {
//                    if (c == '.') {
//                        AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
//                        return Result.STOP;
//                    }
//                }
//            }
//        }

        return Result.CONTINUE;

    }
}
