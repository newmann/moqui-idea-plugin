package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.util.LocationUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import static com.intellij.patterns.PlatformPatterns.psiElement;

/**
 * Transition的来源：
 * 1、transition
 * 2、transition-include
 * 3、SubScreens-item
 * 4、相对路径下的screen
 * 5、代表自己的 .
 *
 */
@Deprecated
public class TransitionCompletionContributor extends CompletionContributor {

  TransitionCompletionContributor(){
    extend(CompletionType.BASIC, getCapture(), new CompletionProvider<>() {
      @Override
      protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        PsiElement psiElement = parameters.getPosition();
        result.addAllElements(findCompletionItem(psiElement));
      }
    });
  }
  private static PsiElementPattern.Capture<PsiElement> getCapture() {
    return psiElement().inside(
            XmlPatterns.xmlAttributeValue(AbstractForm.ATTR_TRANSITION).inside(
                    XmlPatterns.xmlTag().withLocalName(FormSingle.TAG_NAME,FormList.TAG_NAME,DynamicContainer.TAG_NAME,
                            DynamicDialog.TAG_NAME,DynamicOptions.TAG_NAME).inside(
                            XmlPatterns.xmlTag().withLocalName(Screen.TAG_NAME)
                    )
            )
    );
  }

  private List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement){
    List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();

    //添加自身
    lookupElementBuilders.add(
            LookupElementBuilder.create(".")
                    .withCaseSensitivity(false)
                    .withTypeText("current Screen")
                    .withIcon(MoquiIcons.ScreenTag)
    );
    //添加SubScreensItem
    List<SubScreensItem> subScreensItemList = ScreenUtils.getSubScreensItemList(psiElement);
    subScreensItemList.forEach(item -> lookupElementBuilders.add(
            LookupElementBuilder.create(MyDomUtils.getValueOrEmptyString(item.getName()))
                    .withCaseSensitivity(false)
                    .withIcon(MoquiIcons.ScreenTag)
                    .withTypeText(MyDomUtils.getValueOrEmptyString(item.getLocation()))
    ));
    //添加相对路径下的Screen
    List<PsiFile> relativeFileList = LocationUtils.getRelativeScreenFileList(psiElement);
    relativeFileList.forEach(item->{
      if(item.getParent() != null) {
        lookupElementBuilders.add(
                LookupElementBuilder.create(MyStringUtils.removeLastDotString(item.getName()))
                        .withCaseSensitivity(false)
                        .withIcon(MoquiIcons.ScreenTag)
                        .withTypeText(item.getParent().getVirtualFile().getPath())
        );
      }
    });


    return lookupElementBuilders;
  }
}
