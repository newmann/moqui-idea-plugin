package org.moqui.idea.plugin.action.flowManagement

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.psi.PsiElement
import org.moqui.idea.plugin.dom.model.Service
import org.moqui.idea.plugin.util.MyDomUtils

class ShowFlowAnAction: AnAction() {
    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }

    override fun update(e: AnActionEvent) {
        // Get required data keys
        val project = e.project
        val editor = e.getData(CommonDataKeys.EDITOR)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        e.presentation.isEnabledAndVisible = project != null && editor != null && psiFile != null && MyDomUtils.isMoquiXmFile(psiFile)

    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val offset = e.getData(CommonDataKeys.EDITOR)?.caretModel?.offset ?: return

//        val psiElement: PsiElement = e.getData(CommonDataKeys.PSI_ELEMENT) ?: return
        val psiElement = psiFile.findElementAt(offset) ?: return

        val service = MyDomUtils.getLocalDomElementByPsiElement(psiElement,Service::class.java);
        if(service.isPresent) {
            showFlowFrame(project,service.get())
        }

    }
}