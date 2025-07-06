package org.moqui.idea.plugin.typedHandler;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.editorActions.TypedHandlerDelegate;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class ServiceCallTypedHandler extends TypedHandlerDelegate {
    private final static Logger LOGGER = Logger.getInstance(ServiceCallTypedHandler.class);
    @NotNull
    @Override
    public Result charTyped(char c, @NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
//        LOGGER.warn("In ServiceCallTypedHandler, input char"+ c);

//        if(c == '.') {
//            LOGGER.warn("In ServiceCallTypedHandler, begin autoPopupMemberLookup");
//            AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
//            return Result.STOP;
//        }
        if(!MyDomUtils.isMoquiXmlFile(file)) return Result.CONTINUE;
        final PsiElement at = file.findElementAt(editor.getCaretModel().getOffset()-1);
//        if (at == null || !(at.getParent() instanceof XmlTag)) {
        if (at == null) {
            return Result.CONTINUE;
        }
        String tagName = MyDomUtils.getCurrentTagName(at).orElse(MyStringUtils.EMPTY_STRING);
        if (tagName.equals(ServiceCall.TAG_NAME)) {
            switch (c) {
//                case '.' -> {
//                    LOGGER.warn("In ServiceCallTypedHandler, begin autoPopupMemberLookup");
//                    AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
//                    return Result.STOP;
//
//                }
                case '#' -> {
//                    LOGGER.warn("In ServiceCallTypedHandler, begin autoPopupMemberLookup");
                    AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
                    return Result.STOP;

                }
//                case ',','['->{
//                    if(ServiceCallInMapCompletionProvider.IN_MAP_PATTERN.accepts(at)){
//                        AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
//                        return Result.STOP;
//                    }
//
//                }
//                default -> {
//                    if(at.getParent() instanceof XmlTag tag) {
//                        if (tag.getName().equals(ServiceCall.TAG_NAME)) {
//                            if (c == '.') {
//                                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
//                                return Result.STOP;
//                            }
//                        }
//                    }
//
//                }
            }
        }else {
            if(c == '/') {
                AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
                return Result.STOP;
            }
        }
        return Result.CONTINUE;
    }

}
