package org.moqui.idea.plugin.typedHandler;

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.util.MyDomUtils;

public class MoquiEnterTypedHandler implements EnterHandlerDelegate {
        private static final Logger LOGGER = Logger.getInstance(MoquiEnterTypedHandler.class);
        private XmlAttributeValue nextValue;

    @Override
    public Result preprocessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull Ref<Integer> caretOffset, @NotNull Ref<Integer> caretAdvance, @NotNull DataContext dataContext, @Nullable EditorActionHandler originalHandler) {
        //如果是在多个caret情况下，不进行处理
        if(editor.getCaretModel().getCaretCount()>1) {return Result.Continue;}

        if(MyDomUtils.isMoquiXmlFile(file)) {
//            LOGGER.warn("In Moqui xml file, jump to next Attribute value.");
//            PsiDocumentManager.getInstance(file.getProject()).commitDocument(editor.getDocument());
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            PsiElement psiElement = file.findElementAt(caretOffset.get());

            if (psiElement instanceof XmlToken xmlToken) {
                nextValue = MyDomUtils.getSiblingAttributeValue(xmlToken).orElse(null);

                if(nextValue !=null) {

//                    //同时选中所有的内容
//                    SelectionModel selectionModel = editor.getSelectionModel();
//                    selectionModel.removeSelection();
//                    selectionModel.setSelection(nextValue.getValueTextRange().getStartOffset(),nextValue.getValueTextRange().getEndOffset());

//                    caret.removeSelection();
                    caret.moveToOffset(nextValue.getTextOffset());

                    return Result.Stop;
                }
//                if(xmlToken.getTokenType() == XML_ATTRIBUTE_VALUE_TOKEN) {
//
//                }

            }
        }
        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        //如果是在多个caret情况下，不进行处理
        if(editor.getCaretModel().getCaretCount()>1) {return Result.Continue;}

        if(MyDomUtils.isMoquiXmlFile(file)) {
//            LOGGER.warn("In Moqui xml file, jump to next Attribute value.");
            PsiDocumentManager.getInstance(file.getProject()).commitDocument(editor.getDocument());
            Caret caret = editor.getCaretModel().getPrimaryCaret();
            PsiElement psiElement = file.findElementAt(caret.getOffset());

            if (psiElement instanceof XmlToken xmlToken) {
                nextValue = MyDomUtils.getSiblingAttributeValue(xmlToken).orElse(null);

                if(nextValue !=null) {
                    caret.removeSelection();
                    caret.moveToOffset(nextValue.getTextOffset());
//                    //同时选中所有的内容
                    SelectionModel selectionModel = editor.getSelectionModel();
                    selectionModel.removeSelection();
                    selectionModel.setSelection(nextValue.getValueTextRange().getStartOffset(),nextValue.getValueTextRange().getEndOffset());

                    return Result.Stop;
                }

            }
        }
        return Result.Continue;
    }


}
