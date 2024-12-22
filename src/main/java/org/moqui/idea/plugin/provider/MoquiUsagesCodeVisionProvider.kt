package org.moqui.idea.plugin.provider

import com.intellij.codeInsight.codeVision.CodeVisionRelativeOrdering
import com.intellij.codeInsight.hints.codeVision.CodeVisionProviderBase
import com.intellij.codeInsight.navigation.actions.GotoDeclarationAction
import com.intellij.java.JavaBundle
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiReference
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.xml.XmlAttributeValue
import com.intellij.psi.xml.XmlTag
import com.intellij.ui.awt.RelativePoint
import com.intellij.util.Query
import com.intellij.util.xml.GenericAttributeValue
import org.moqui.idea.plugin.dom.model.*
import org.moqui.idea.plugin.util.MyDomUtils
import org.moqui.idea.plugin.util.RestApiUtils
import java.awt.event.MouseEvent

class MoquiUsagesCodeVisionProvider: CodeVisionProviderBase() {
    val CHECK_TAG: Set<String> = setOf(Entity.TAG_NAME,ViewEntity.TAG_NAME, Service.TAG_NAME,
        Transition.TAG_NAME,TransitionInclude.TAG_NAME, WidgetTemplate.TAG_NAME)
    override val id: String
        get() = "Moqui.references"
    override val name: String
        get() = "Usages"
    override val relativeOrderings: List<CodeVisionRelativeOrdering>
        get() = listOf(CodeVisionRelativeOrdering.CodeVisionRelativeOrderingBefore("java.inheritors"))

    override fun acceptsElement(element: PsiElement): Boolean {
        if (element is XmlTag) {
            return CHECK_TAG.contains(element.name)
        }
        return false
    }


    override fun acceptsFile(file: PsiFile): Boolean = MyDomUtils.isMoquiXmFile(file) && !RestApiUtils.isRestApiFile(file) //将Rest接口定义排除在外

    override fun getHint(element: PsiElement, file: PsiFile): String? {
        return getVisionInfo(element)?.text
    }


    fun getVisionInfo(element: PsiElement): CodeVisionInfo? {
        if(element is XmlTag) {
            val totalUsageCount = findReferences(getXmlAttributeValue(element))
            return CodeVisionInfo(JavaBundle.message("usages.telescope", totalUsageCount), totalUsageCount) //todo 如何合并JavaBundle
        }
        return null

    }
    private fun getXmlAttributeValue(xmlTag: XmlTag): XmlAttributeValue?{
        return when (xmlTag.name) {
            Entity.TAG_NAME -> MyDomUtils.getLocalDomElementByPsiElement(xmlTag, Entity::class.java, false).map(Entity::getEntityName).map(GenericAttributeValue<String>::getXmlAttributeValue).orElse(null)
            ViewEntity.TAG_NAME -> MyDomUtils.getLocalDomElementByPsiElement(xmlTag, ViewEntity::class.java, false).map(ViewEntity::getEntityName).map(GenericAttributeValue<String>::getXmlAttributeValue).orElse(null)
            Service.TAG_NAME -> MyDomUtils.getLocalDomElementByPsiElement(xmlTag, Service::class.java, false).map(Service::getVerb).map(GenericAttributeValue<String>::getXmlAttributeValue).orElse(null)
            Transition.TAG_NAME->MyDomUtils.getLocalDomElementByPsiElement(xmlTag, Transition::class.java, false).map(Transition::getName).map(GenericAttributeValue<String>::getXmlAttributeValue).orElse(null)
            TransitionInclude.TAG_NAME->MyDomUtils.getLocalDomElementByPsiElement(xmlTag, TransitionInclude::class.java, false).map(TransitionInclude::getName).map(GenericAttributeValue<String>::getXmlAttributeValue).orElse(null)
            WidgetTemplate.TAG_NAME->MyDomUtils.getLocalDomElementByPsiElement(xmlTag, WidgetTemplate::class.java, false).map(WidgetTemplate::getName).map(GenericAttributeValue<String>::getXmlAttributeValue).orElse(null)
            else-> null
        }
    }
    private fun findReferences(element: XmlAttributeValue?):Int {
        if(element == null) {
            return 0
        }else {
//            val searchScope = GlobalSearchScope.allScope(element.project)
            val searchScope = GlobalSearchScope.projectScope(element.project) //仅限项目源代码：不包括库和外部依赖

            val searchParameters = ReferencesSearch.SearchParameters(element, searchScope, false)

            val referencesQuery: Query<PsiReference> = ReferencesSearch.search(searchParameters)
            val usages: MutableCollection<PsiReference> = referencesQuery.findAll()
            return usages.size
        }
    }
    override fun handleClick(editor: Editor, element: PsiElement, event: MouseEvent?) {
        if(element is XmlTag) {
            val value = getXmlAttributeValue(element)
            if(value != null) {
                GotoDeclarationAction.startFindUsages(editor, element.project, value.originalElement, if (event == null) null else RelativePoint(event))
            }
        }

    }
    /**
     * Code vision item information
     * @param text Label of the item that is displayed in the interline
     * @param count If the item represents a counter, the count, null otherwise
     * @param countIsExact Whether the counter represents the exact count or a lower bound estimate
     *    (the latter can happen if computing the exact count is slow)
     */
    class CodeVisionInfo(
        val text: String,
        val count: Int? = null,
        val countIsExact: Boolean = true
    )
}