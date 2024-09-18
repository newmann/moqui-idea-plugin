package org.moqui.idea.plugin.action.flowManagement

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import org.moqui.idea.plugin.dom.model.Actions
import org.moqui.idea.plugin.dom.model.Service
import org.moqui.idea.plugin.util.MyDomUtils

class ShowFlowAnAction: AnAction() {
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
        //只有actions才显示菜单
        val actions = MyDomUtils.getLocalDomElementByPsiElement(psiElement, Actions::class.java)
        e.presentation.isEnabledAndVisible = MyDomUtils.isMoquiXmFile(psiFile) && actions.isPresent

    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val psiFile = e.getData(CommonDataKeys.PSI_FILE) ?: return
        val offset = e.getData(CommonDataKeys.EDITOR)?.caretModel?.offset ?: return

//        val psiElement: PsiElement = e.getData(CommonDataKeys.PSI_ELEMENT) ?: return
        val psiElement = psiFile.findElementAt(offset) ?: return

        val service = MyDomUtils.getLocalDomElementByPsiElement(psiElement,Service::class.java)
        if(service.isPresent) {
            showFlowFrame(project,service.get())
        }else {
            val actions = MyDomUtils.getLocalDomElementByPsiElement(psiElement,Actions::class.java)
            if(actions.isPresent) {
                showFlowFrame(project,actions.get())
            }
        }

    }
}