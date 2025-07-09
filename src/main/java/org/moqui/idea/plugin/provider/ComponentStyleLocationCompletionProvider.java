package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.insertHandler.AutoShowByCharInsertHandler;
import org.moqui.idea.plugin.insertHandler.ClearTailInsertHandler;
import org.moqui.idea.plugin.util.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 类似component://xxx/xxx.xml的路径补全
 *
 */
public class ComponentStyleLocationCompletionProvider extends CompletionProvider<CompletionParameters> {
    public static ComponentStyleLocationCompletionProvider of(){
        return new ComponentStyleLocationCompletionProvider();
    }

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> SUB_SCREENS_ITEM_PATTERN =
            PlatformPatterns.psiElement().inside(
                    XmlPatterns.xmlAttributeValue(SubScreensItem.ATTR_LOCATION)
                            .withSuperParent(2,
                                XmlPatterns.xmlTag().withLocalName(SubScreensItem.TAG_NAME).inside(
                                        XmlPatterns.xmlTag().withLocalName(Screen.TAG_NAME)
                                )
                    )
            );


    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        PsiElement psiElement = parameters.getPosition();
        if(!MyDomUtils.isMoquiProject(psiElement.getProject())) return;
        Project project = psiElement.getProject();

        BeginAndEndCharPattern charPattern = BeginAndEndCharPattern.of(psiElement);
        String inputString = MyStringUtils.getDummyFrontString(charPattern.getContent());
//        char inputChar = inputString.charAt(inputString.length()-1);
        Location location = Location.of(project, inputString);
        if(location.getType() != LocationType.ComponentFile) {
            resultSet.addAllElements(createRootCompletionItem(psiElement));
            return;
        }

//        String allString = MyStringUtils.removeDummy(charPattern.getContent());
//        boolean inputAtEnd = inputString.equals(allString);



        lookupUrl(project,inputString,psiElement, resultSet);
    }
    private void lookupUrl(@NotNull Project project, @NotNull String inputStr, @NotNull PsiElement psiElement, @NotNull CompletionResultSet resultSet){
        resultSet = resultSet.withPrefixMatcher(new IgnorePathPrefixMatcher(inputStr));

        String purePath = MyStringUtils.getParentPath(inputStr);

        Location location = Location.of(project, purePath);
        List<VirtualFile> virtualFileList = ComponentUtils.getChildVirtualFileByLocation(psiElement.getProject(),location);
        for(VirtualFile virtualFile: virtualFileList) {

            if(virtualFile.isDirectory()) {
                resultSet.addElement(
                        LookupElementBuilder.create(virtualFile.getName())
                                .withCaseSensitivity(true)
                                .withIcon(AllIcons.Nodes.Folder)
                                .withInsertHandler(AutoShowByCharInsertHandler.ofPathSeparator())
                );

            }else {
                //如果不是Moqui的文件，则直接跳过
                PsiFile psiFile = MyDomUtils.getPsiFileFromVirtualFile(project,virtualFile).orElse(null);
                if(psiFile == null) continue;
                if(!MyDomUtils.isSpecialXmlFile(psiFile,Screen.TAG_NAME)) continue;
                resultSet.addElement(
                        LookupElementBuilder.create(virtualFile.getName())
                                .withCaseSensitivity(true)
                                .withIcon(MyIcons.ScreenTag)
                                .withInsertHandler(ClearTailInsertHandler.of())
                );

            }
        }
    }
    private List<LookupElementBuilder> createRootCompletionItem(@NotNull PsiElement psiElement) {
        List<LookupElementBuilder> lookupElementBuilders = new ArrayList<>();

        Map<String, Component> componentMap = ComponentUtils.findAllComponent(psiElement.getProject());

        String typePath = "";
        if(SUB_SCREENS_ITEM_PATTERN.accepts(psiElement)) typePath = "/screen";

        final String finalTypePath = typePath;
        componentMap.forEach((key, value) -> {
            lookupElementBuilders.add(
                    LookupElementBuilder.create(MyStringUtils.COMPONENT_LOCATION_PREFIX + key + finalTypePath)
                            .withCaseSensitivity(true)
                            .withInsertHandler(AutoShowByCharInsertHandler.ofPathSeparator())
                            .withIcon(MyIcons.ComponentTag)
            );
        });



        return lookupElementBuilders;
    }

}
