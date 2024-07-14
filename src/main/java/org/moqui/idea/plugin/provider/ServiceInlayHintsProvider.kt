package org.moqui.idea.plugin.provider

import com.intellij.codeInsight.hints.declarative.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.parentOfType
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.refactoring.suggested.endOffset
import com.intellij.refactoring.suggested.startOffset
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrCall
import org.jetbrains.plugins.groovy.lang.psi.util.PsiUtil
import org.moqui.idea.plugin.util.ServiceUtils

class ServiceInlayHintsProvider:InlayHintsProvider {
    override fun createCollector(file: PsiFile, editor: Editor): InlayHintsCollector? {
        if(ServiceUtils.isServicesFile(file)) {
           return ServiceHintsCollector()
        }else {
            return null
        }
    }

    class ServiceHintsCollector : SharedBypassCollector {
        override fun collectFromElement(element: PsiElement, sink: InlayTreeSink) {
            if (element !is XmlAttributeValue) {
                return
            }
            sink.addPresentation(InlineInlayPosition(element.startOffset, relatedToPrevious = true), hasBackground = true) {
                text("测试一下")
            }
        }
    }
}