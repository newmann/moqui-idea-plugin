package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.insertHandler.ScreenIncludeInsertObject;
import org.moqui.idea.plugin.dom.converter.insertHandler.ScreenIncludeInsertionHandler;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.dom.model.Section;
import org.moqui.idea.plugin.util.LocationUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ScreenUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 列出所有可用的section（包括section-iterate）
 *
 * 选定后，自动将对应文件名和sectionName插入section-include的属性中
 *
 */
public class SectionIncludeCompletionProvider extends AbstractSimpleCompletionProvider {
    public static SectionIncludeCompletionProvider of(){
        return new SectionIncludeCompletionProvider();
    }

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> SECTION_INCLUDE_PATTERN =
            PlatformPatterns.psiElement().inside(
                    XmlPatterns.xmlAttributeValue(Section.ATTR_NAME).inside(
                            XmlPatterns.xmlTag().withLocalName(Section.TAG_NAME).inside(
                                    XmlPatterns.xmlTag().withLocalName(Screen.TAG_NAME)
                            )
                    )
            );
    @Override
    public List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement) {
        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();

        List<DomFileElement<Screen>> fileElementList  = MyDomUtils.findDomFileElementsByRootClass(psiElement.getProject(), Screen.class);
        for(DomFileElement<Screen> screenDomFileElement: fileElementList) {
            LocationUtils.MoquiFile moquiFile = LocationUtils.ofMoquiFile(screenDomFileElement.getFile().getContainingFile());
            for(Section section: ScreenUtils.getSectionListFromScreenFile(screenDomFileElement)) {
                String sectionName = MyDomUtils.getValueOrEmptyString(section.getName());
                String lookupString = LocationUtils.simplifyComponentRelativePath(moquiFile.getRelativePath())+ ServiceUtils.SERVICE_NAME_HASH + sectionName;
                ScreenIncludeInsertObject screenIncludeInsertObject = ScreenIncludeInsertObject.of(moquiFile.getComponentName(),
                        moquiFile.getRelativePath(),sectionName);
                lookupElementBuilders.add(
                        LookupElementBuilder.create(screenIncludeInsertObject, lookupString)
                                .withInsertHandler(ScreenIncludeInsertionHandler.INSTANCE)
                                .withCaseSensitivity(false)
                );

            }
        }
        return lookupElementBuilders;
    }
}
