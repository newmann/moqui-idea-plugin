package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.moqui.idea.plugin.MyIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.dom.model.SubScreens;
import org.moqui.idea.plugin.dom.model.SubScreensItem;
import org.moqui.idea.plugin.util.LocationUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * SubScreen DefaultItem指向自己定义的subscreen或相对路径下的screen
 *
 */
public class SubScreensDefaultItemCompletionProvider extends AbstractSimpleCompletionProvider {
    public static SubScreensDefaultItemCompletionProvider of(){
        return new SubScreensDefaultItemCompletionProvider();
    }

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> SUB_SCREENS_DEFAULT_ITEM_PATTERN =
            PlatformPatterns.psiElement().inside(
                    XmlPatterns.xmlAttributeValue(SubScreens.ATTR_DEFAULT_ITEM).inside(
                            XmlPatterns.xmlTag().withLocalName(SubScreens.TAG_NAME).inside(
                                    XmlPatterns.xmlTag().withLocalName(Screen.TAG_NAME)
                            )
                    )
            );
    @Override
    public List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement) {
        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();


        //添加SubScreensItem
        List<SubScreensItem> subScreensItemList = ScreenUtils.getSubScreensItemList(psiElement);
        subScreensItemList.forEach(item ->{
            lookupElementBuilders.add(
                    LookupElementBuilder.create(MyDomUtils.getValueOrEmptyString(item.getName()))
                            .withCaseSensitivity(false)
                            .withIcon(MyIcons.ScreenTag)
                            .withTypeText(MyDomUtils.getValueOrEmptyString(item.getLocation()))
            );

        });
        //添加相对路径下的Screen
        List<PsiFile> relativeFileList = LocationUtils.getRelativeScreenFileList(psiElement);
        relativeFileList.forEach(item->{
            if(item.getParent()!= null) {
                lookupElementBuilders.add(
                        LookupElementBuilder.create(MyStringUtils.removeLastDotString(item.getName()))
                                .withCaseSensitivity(false)
                                .withIcon(MyIcons.ScreenTag)
                                .withTypeText(
                                        LocationUtils.simplifyComponentRelativePath(
                                            LocationUtils.extractComponentRelativePath(item.getParent().getVirtualFile().getPath()).orElse(MyStringUtils.EMPTY_STRING)
                                        )
                                )
                );
            }
        });

        return lookupElementBuilders;
    }
}
