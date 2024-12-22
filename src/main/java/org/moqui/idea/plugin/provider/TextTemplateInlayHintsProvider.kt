package org.moqui.idea.plugin.provider

import com.intellij.codeInsight.hints.declarative.*
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.xml.XmlAttribute
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.ui.components.Label
import org.moqui.idea.plugin.reference.TextTemplateReference
import org.moqui.idea.plugin.util.MyDomUtils

class TextTemplateInlayHintsProvider:InlayHintsProvider {
    override fun createCollector(file: PsiFile, editor: Editor): InlayHintsCollector? {
        return if(MyDomUtils.isMoquiXmFile(file))
            Collector()
        else
            null
    }
    private class Collector:SharedBypassCollector{
        override fun collectFromElement(element: PsiElement, sink: InlayTreeSink) {
            if(element is XmlAttributeValue) {
                val xmlAttribute = element.parent as? XmlAttribute
                if(xmlAttribute?.name.equals("text")) {
//                    val xmlTag = MyDomUtils.getParentTag(element).orElse(null);
//                    if (xmlTag?.name.equals(org.moqui.idea.plugin.dom.model.Label.TAG_NAME)) {
//
                        if(element.reference is TextTemplateReference) {
                            sink.addPresentation(
                                InlineInlayPosition(element.textRange.startOffset + 1, true),
                                hasBackground = true
                            ) {
                                text("(LocalizedMessage)")
                            }
                        }
//                    }
                }
            }
        }
    }

}