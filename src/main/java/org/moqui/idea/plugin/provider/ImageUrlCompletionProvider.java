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
 * 支持Image的url
 * 需要按路径找到image文件
 * 还需要支持transition
 *
 */
public class ImageUrlCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final static Logger LOGGER = Logger.getInstance(ImageUrlCompletionProvider.class);

    public static ImageUrlCompletionProvider of(){
        return new ImageUrlCompletionProvider();
    }

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> IMAGE_URL_PATTERN =
            PlatformPatterns.psiElement().inside(
                    XmlPatterns.xmlAttributeValue().andOr(
                            XmlPatterns.xmlAttributeValue(AbstractUrl.ATTR_URL).withSuperParent(2,
                                    XmlPatterns.xmlTag().withLocalName(
                                            Image.TAG_NAME
                                        ).inside(
                                            XmlPatterns.xmlTag().withLocalName(Screen.TAG_NAME)
                                    )
                            ),
                            XmlPatterns.xmlAttributeValue(ScreenBase.ATTR_MENU_IMAGE).withSuperParent(2,
                                    XmlPatterns.xmlTag().withLocalName(
                                            Screen.TAG_NAME,ScreenExtend.TAG_NAME
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

        String purePath = MyStringUtils.getParentPath(inputStr);

        Location location = Location.of(project, purePath);
        MoquiUrl moquiUrl;
        switch (location.getType()) {
            case  AbsoluteUrl ->{
//                String path;
//                if(inputStr.equals(MyStringUtils.BASE_URL) ||
//                        inputStr.equals(MyStringUtils.ROOT_URL) ||
//                        (location.getPathNameArray().length ==1) && !inputStr.endsWith(MyStringUtils.PATH_SEPARATOR) ) {
//                    path = MyStringUtils.ROOT_SCREEN_LOCATION;
//                }else {
//                    path = purePath;
//                }
//                LOGGER.warn("Current inputStr:" + inputStr +" path：" + path);

                moquiUrl = MoquiUrl.ofAbsoluteUrl(location,false);

            }
            case RelativeUrl -> {
                moquiUrl = MoquiUrl.ofRelativeUrl(psiElement, purePath, false);
            }
            case TransitionLevelName -> {
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

        if(moquiUrl != null) {
            addChildUrlElement(moquiUrl, result);
            }
    }
    private void addChildUrlElement(@NotNull MoquiUrl moquiUrl, @NotNull CompletionResultSet resultSet) {
//        LOGGER.warn("开始addChildUrl element，moquiUrl："+moquiUrl.getName());

        for(MoquiUrl childUrl: moquiUrl.getNextLevelChildren()) {
            //判断是不是图像文件，如果不是，则跳过
            if(moquiUrl.getContainingMoquiFile() != null && moquiUrl.getScreen()==null && moquiUrl.getSubScreensItem() == null ) {
                if(! moquiUrl.getContainingMoquiFile().isImageFile()) continue;
            }
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

//
//        lookupElementBuilders.add(
//                LookupElementBuilder.create(MyStringUtils.CURRENT_PATH)
//                        .withCaseSensitivity(true)
//                        .withTypeText("Current screen")
//                        .withIcon(MyIcons.ScreenTag)
//        );
//        lookupElementBuilders.add(
//                LookupElementBuilder.create(MyStringUtils.PARENT_PATH)
//                        .withCaseSensitivity(true)
//                        .withTypeText("Parent screen")
//                        .withIcon(MyIcons.ScreenTag)
//        );

        lookupElementBuilders.add(
                LookupElementBuilder.create(MyStringUtils.ROOT_URL)
                        .withCaseSensitivity(true)
                        .withInsertHandler(AutoPopupInsertHandler.of())
//                        .withTypeText("current Screen")
//                        .withIcon(MyIcons.ScreenTag)
        );
//        lookupElementBuilders.add(
//                LookupElementBuilder.create(MyStringUtils.BASE_URL)
//                        .withCaseSensitivity(true)
//                        .withInsertHandler(AutoPopupInsertHandler.of())
////                        .withTypeText("current Screen")
////                        .withIcon(MyIcons.ScreenTag)
//        );
        //添加Transition
        ScreenUtils.getAbstractTransitionListFromPsiElement(psiElement).forEach(
                item -> lookupElementBuilders.add(
                        LookupElementBuilder.create(MyDomUtils.getValueOrEmptyString(item.getName()))
                                .withCaseSensitivity(true)
                                .withIcon(MyIcons.TransitionTag)
                                .withTypeText(Transition.TAG_NAME)
                )
        );


        return lookupElementBuilders;
    }


}
