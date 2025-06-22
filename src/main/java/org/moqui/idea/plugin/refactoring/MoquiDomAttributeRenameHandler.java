package org.moqui.idea.plugin.refactoring;

import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.refactoring.rename.PsiElementRenameHandler;
import com.intellij.refactoring.rename.RenameDialog;
import org.jetbrains.annotations.NotNull;

/**
 * 通用的属性修改处理过程
 */
public class MoquiDomAttributeRenameHandler extends PsiElementRenameHandler {
    @Override
    public boolean isAvailableOnDataContext(@NotNull DataContext dataContext) {
        return findTarget(dataContext) != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file, @NotNull DataContext dataContext) {
        invoke(project, PsiElement.EMPTY_ARRAY, dataContext);
    }

    @Override
    public void invoke(@NotNull Project project,  @NotNull PsiElement[] elements, DataContext dataContext) {
        PsiElement element = elements.length == 1 ? elements[0] : null;
        if (element == null) element = findTarget(dataContext);

        if(element != null) {
            RenameDialog.showRenameDialog(dataContext, new RenameDialog(project, element, null, CommonDataKeys.EDITOR.getData(dataContext)));

        }
    }

    private static PsiElement findTarget(DataContext dataContext){
        Editor editor = CommonDataKeys.EDITOR.getData(dataContext);
        if(editor==null) return null;
        PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(dataContext);
        if(psiFile==null) return null;

        PsiElement psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
        if(psiElement==null) return null;
        XmlAttributeValue attributeValue = PsiTreeUtil.getParentOfType(psiElement, XmlAttributeValue.class);
        if(attributeValue==null) return null;

        //表示当前的element指向了一个PsiElement，是converter生成，不需要进行处理，由缺省的converter过程进行处理
        if(attributeValue.getReference()== null) {
            return attributeValue;
        }else {
            return null;
        }


    }
}
