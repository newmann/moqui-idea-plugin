package org.moqui.idea.plugin.action.formGenerator

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.psi.PsiElement
import org.jetbrains.annotations.NotNull
import org.moqui.idea.plugin.dom.model.Actions
import org.moqui.idea.plugin.dom.model.Entity
import org.moqui.idea.plugin.dom.model.Service
import org.moqui.idea.plugin.util.EntityUtils
import org.moqui.idea.plugin.util.MyDomUtils
import org.moqui.idea.plugin.util.MyStringUtils
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class ShowFormGeneratorAnAction: AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        // Get required data keys
//        val project = e.project ?: return
//        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val offset = e.getData(CommonDataKeys.EDITOR)?.caretModel?.offset ?: return
        val psiElement =  psiFile.findElementAt(offset) ?: return
        e.presentation.isEnabledAndVisible = MyDomUtils.isMoquiXmFile(psiFile) && shouldUpdate(psiElement)

    }

    private fun shouldUpdate(@NotNull psiElement: PsiElement):Boolean{
        val entity = MyDomUtils.getLocalDomElementByPsiElement(psiElement, Entity::class.java)
        return entity.isPresent
    }
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val offset = e.getData(CommonDataKeys.EDITOR)?.caretModel?.offset ?: return

//        val psiElement: PsiElement = e.getData(CommonDataKeys.PSI_ELEMENT) ?: return
        val psiElement = psiFile.findElementAt(offset) ?: return

        val entity = MyDomUtils.getLocalDomElementByPsiElement(psiElement, Entity::class.java)
        if(entity.isPresent) {
            val entityFormGenerator = EntityFormGenerator.INSTANCE;
            val indexEntity = EntityUtils.getIndexEntityByName(project,EntityUtils.getFullNameFromEntity(entity.get())).orElse(null)
            val content = entityFormGenerator.generatorFormSingle(project,indexEntity,FormSingleGenerateType.Create).orElse(MyStringUtils.EMPTY_STRING)
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard;

            clipboard.setContents(StringSelection(content),null)
            Messages.showMessageDialog(
                project,
                "Form is generated to Clipboard, Please copy to target Screen file.",
                "Moqui Idea Plugin",
                Messages.getInformationIcon())
        }

    }
}