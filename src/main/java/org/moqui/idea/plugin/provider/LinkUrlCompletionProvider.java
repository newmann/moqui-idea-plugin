package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.insertHandler.AutoPopupInsertHandler;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 支持Link的url
 * 不仅需要支持UrlCompletionProvider的内容
 * 还需要支持transition
 *
 */
public class LinkUrlCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final static Logger LOGGER = Logger.getInstance(LinkUrlCompletionProvider.class);

    public static LinkUrlCompletionProvider of(){
        return new LinkUrlCompletionProvider();
    }

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> LINK_URL_PATTERN =
            PlatformPatterns.psiElement().inside(
                    XmlPatterns.xmlAttributeValue().andOr(
                            XmlPatterns.xmlAttributeValue(AbstractUrl.ATTR_URL).withSuperParent(2,
                                    XmlPatterns.xmlTag().withLocalName(
                                            Link.TAG_NAME
                                        ).inside(
                                            XmlPatterns.xmlTag().withLocalName(Screen.TAG_NAME)
                                    )
                            )
                    )
            );
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        PsiElement psiElement = parameters.getPosition();
        if(!MyDomUtils.isMoquiProject(psiElement.getProject())) return;


        BeginAndEndCharPattern charPattern = BeginAndEndCharPattern.of(psiElement);
        String inputString = MyStringUtils.getDummyFrontString(charPattern.getContent());
//        char inputChar = inputString.charAt(inputString.length()-1);
        if(MyStringUtils.isEmpty(inputString)) {
            resultSet.addAllElements(createRootCompletionItem(psiElement));
            return;
        }

//        String allString = MyStringUtils.removeDummy(charPattern.getContent());
//        boolean inputAtEnd = inputString.equals(allString);

        Project project = psiElement.getProject();

        lookupUrl(project,inputString,psiElement, resultSet);

    }
    private void lookupUrl(@NotNull Project project, @NotNull String inputStr, @NotNull PsiElement psiElement, @NotNull CompletionResultSet result){
        result = result.withPrefixMatcher(new IgnorePathPrefixMatcher(inputStr));

        String purePath = MyStringUtils.removeLastPath(inputStr);

        LocationUtils.Location location = LocationUtils.ofLocation(project, purePath);
        MoquiUrl moquiUrl;
        switch (location.getType()) {
            case  AbsoluteUrl ->{
                String path;
                if(inputStr.equals(MyStringUtils.BASE_URL) ||
                        inputStr.equals(MyStringUtils.ROOT_URL) ||
                        (location.getPathNameArray().length ==1) && !inputStr.endsWith(MyStringUtils.PATH_SEPARATOR) ) {
                    path = MyStringUtils.ROOT_SCREEN_LOCATION;
                }else {
                    path = purePath;
                }
                LOGGER.warn("Current inputStr:" + inputStr +" path：" + path);

                moquiUrl = MoquiUrl.of(project,path,false);

            }
            case RelativeUrl -> {
                moquiUrl = MoquiUrl.of(psiElement, purePath, false);
            }
            case TransitionName -> {
                //添加Transition
                for(AbstractTransition item : ScreenUtils.getAbstractTransitionListFromPsiElement(psiElement)) {
                    result.addElement(
                            LookupElementBuilder.create(MyDomUtils.getValueOrEmptyString(item.getName()))
                                    .withCaseSensitivity(true)
                                    .withIcon(MyIcons.TransitionTag)
                                    .withTypeText(Transition.TAG_NAME)
                    );
                };
                return;
            }
            default -> {
                moquiUrl = null;
            }
        }
//        String path;
//        if(inputStr.equals(MyStringUtils.BASE_URL) ||
//                inputStr.equals(MyStringUtils.ROOT_URL) ||
//                    (location.getPathNameArray().length ==1) && !inputStr.endsWith(MyStringUtils.PATH_SEPARATOR) ) {
//            path = MyStringUtils.ROOT_SCREEN_LOCATION;
//        }else {
//            path = inputStr.substring(0, inputStr.lastIndexOf(MyStringUtils.PATH_SEPARATOR));
//        }
//        LOGGER.warn("Current inputStr:" + inputStr +" path：" + path);
//
//        MoquiUrl moquiUrl = MoquiUrl.of(project,path,false);

        if(moquiUrl != null) {
            addChildUrlElement(moquiUrl, result);
            //相对路径可以找多次上级目录
            int pathNameArrayLength = location.getPathNameArray().length;
            if(pathNameArrayLength >=1)
                if(location.getPathNameArray()[pathNameArrayLength - 1].equals(MyStringUtils.PARENT_PATH)) {
                    result.addElement(
                            LookupElementBuilder.create(MyStringUtils.PARENT_PATH)
                                    .withCaseSensitivity(true)
                                    .withTypeText("Parent screen")
                                    .withIcon(MyIcons.ScreenTag)
                    );
                }
            }
    }
    private void addChildUrlElement(@NotNull MoquiUrl moquiUrl, @NotNull CompletionResultSet resultSet) {
//        LOGGER.warn("开始addChildUrl element，moquiUrl："+moquiUrl.getName());

        for(MoquiUrl childUrl: moquiUrl.getNextLevelChildren()) {
            String lookupString = childUrl.getName();
            LookupElementBuilder lookupElementBuilder = LookupElementBuilder.create(moquiUrl, lookupString)
                    .withCaseSensitivity(true)
                    .withIcon(childUrl.getIcon())
                    .withTypeText(childUrl.getTitle());
            resultSet.addElement(lookupElementBuilder);
        }
    }

    private List<LookupElementBuilder> createRootCompletionItem(@NotNull PsiElement psiElement) {
        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();


        lookupElementBuilders.add(
                LookupElementBuilder.create(MyStringUtils.CURRENT_PATH)
                        .withCaseSensitivity(true)
                        .withTypeText("Current screen")
                        .withIcon(MyIcons.ScreenTag)
        );
        lookupElementBuilders.add(
                LookupElementBuilder.create(MyStringUtils.PARENT_PATH)
                        .withCaseSensitivity(true)
                        .withTypeText("Parent screen")
                        .withIcon(MyIcons.ScreenTag)
        );

        lookupElementBuilders.add(
                LookupElementBuilder.create(MyStringUtils.ROOT_URL)
                        .withCaseSensitivity(true)
                        .withInsertHandler(AutoPopupInsertHandler.of())
//                        .withTypeText("current Screen")
//                        .withIcon(MyIcons.ScreenTag)
        );
        lookupElementBuilders.add(
                LookupElementBuilder.create(MyStringUtils.BASE_URL)
                        .withCaseSensitivity(true)
                        .withInsertHandler(AutoPopupInsertHandler.of())
//                        .withTypeText("current Screen")
//                        .withIcon(MyIcons.ScreenTag)
        );
        //添加Transition
        ScreenUtils.getAbstractTransitionListFromPsiElement(psiElement).forEach(
                item -> lookupElementBuilders.add(
                        LookupElementBuilder.create(MyDomUtils.getValueOrEmptyString(item.getName()))
                                .withCaseSensitivity(true)
                                .withIcon(MyIcons.TransitionTag)
                                .withTypeText(Transition.TAG_NAME)
                )
        );

        //添加SubScreensItem
        ScreenUtils.getSubScreensItemList(psiElement).forEach(
                item -> lookupElementBuilders.add(
                        LookupElementBuilder.create(MyDomUtils.getValueOrEmptyString(item.getName()))
                                .withCaseSensitivity(true)
                                .withIcon(MyIcons.ScreenTag)
                                .withTypeText(MyDomUtils.getValueOrEmptyString(item.getLocation()))
                ));
        //添加相对路径下的Screen
        LocationUtils.getRelativeScreenFileList(psiElement).forEach(
                item->{
                    if(item.getParent() != null) {
                        lookupElementBuilders.add(
                                LookupElementBuilder.create(MyStringUtils.removeLastDotString(item.getName()))
                                        .withCaseSensitivity(true)
                                        .withIcon(MyIcons.ScreenTag)
                                        .withTypeText(item.getParent().getVirtualFile().getPath())
                        );
                    }
                });



        return lookupElementBuilders;
    }


}
