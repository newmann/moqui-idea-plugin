package org.moqui.idea.plugin.provider

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering
import com.intellij.codeInsight.hints.codeVision.CodeVisionProviderBase
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlTag
import org.moqui.idea.plugin.dom.model.Entity
import org.moqui.idea.plugin.dom.model.ViewEntity
import org.moqui.idea.plugin.util.EntityUtils
import java.awt.event.MouseEvent

class EntityBlockInlayHintsProvider: CodeVisionProviderBase() {
    override val id: String
        get() = "moqui.entity"
    override val name: String
        get() = "moqui"
    override val relativeOrderings: List<CodeVisionRelativeOrdering>
        get() = emptyList()

    override fun acceptsElement(element: PsiElement): Boolean {
        if(element is XmlTag){
            if((element.name == Entity.TAG_NAME) || (element.name == ViewEntity.TAG_NAME)) return true
        }
        return false
    }


    override fun acceptsFile(file: PsiFile): Boolean = EntityUtils.isEntitiesFile(file);

    override fun getHint(element: PsiElement, file: PsiFile): String? {
        return getVisionInfo(element,file)?.text;
    }

    override fun getVisionInfo(element: PsiElement, file: PsiFile): CodeVisionInfo? {
        if(element is XmlTag) {
            return CodeVisionInfo("Test",10)
        }
        return null
    }

    override fun handleClick(editor: Editor, element: PsiElement, event: MouseEvent?) {
        TODO("Not yet implemented")
    }
}