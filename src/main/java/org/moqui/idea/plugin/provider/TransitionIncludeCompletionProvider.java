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
import org.moqui.idea.plugin.dom.model.Transition;
import org.moqui.idea.plugin.dom.model.TransitionInclude;
import org.moqui.idea.plugin.util.LocationUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.ArrayList;
import java.util.List;
/**
 * 列出所有可用的transition，采用文件名#transitionName的方式
 *
 * 选定后，自动将对应文件名和transitionName插入transition-include的属性中
 *
 */
public class TransitionIncludeCompletionProvider extends AbstractSimpleCompletionProvider {
    public static TransitionIncludeCompletionProvider of(){
        return new TransitionIncludeCompletionProvider();
    }

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> TRANSITION_INCLUDE_PATTERN =
            PlatformPatterns.psiElement().inside(
                    XmlPatterns.xmlAttributeValue(TransitionInclude.ATTR_NAME).inside(
                            XmlPatterns.xmlTag().withLocalName(TransitionInclude.TAG_NAME).inside(
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
            for(Transition transition: screenDomFileElement.getRootElement().getTransitionList()) {
                String transitionName = MyDomUtils.getValueOrEmptyString(transition.getName());
                String lookupString = LocationUtils.simplifyComponentRelativePath(moquiFile.getRelativePath())+ ServiceUtils.SERVICE_NAME_HASH + transitionName;
                ScreenIncludeInsertObject transitionIncludeInsertObject = ScreenIncludeInsertObject.of(moquiFile.getComponentName(),
                        moquiFile.getRelativePath(),transitionName);
                lookupElementBuilders.add(
                        LookupElementBuilder.create(transitionIncludeInsertObject, lookupString)
                                .withInsertHandler(ScreenIncludeInsertionHandler.INSTANCE)
                                .withCaseSensitivity(false)
                );

            }
        }
        return lookupElementBuilders;
    }
}
