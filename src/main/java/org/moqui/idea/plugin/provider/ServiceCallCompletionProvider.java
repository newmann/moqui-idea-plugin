package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import kotlin.OverloadResolutionByLambdaReturnType;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.MyIcons;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.insertHandler.AutoShowByCharInsertHandler;
import org.moqui.idea.plugin.insertHandler.ClearTailInsertHandler;
import org.moqui.idea.plugin.util.*;

import java.util.*;

public class ServiceCallCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final  static Logger LOGGER = Logger.getInstance(ServiceCallCompletionProvider.class);
    public static ServiceCallCompletionProvider of(){
        return new ServiceCallCompletionProvider() ;
    }
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> SERVICE_CALL_PATTERN =
            PlatformPatterns.psiElement()
                    .inside(
                    XmlPatterns.xmlAttributeValue(ServiceCall.ATTR_NAME)
//                            .inside(
//                            XmlPatterns.xmlTag().withLocalName(ServiceCall.TAG_NAME).inside(
//                                    XmlPatterns.xmlTag().withLocalName(Screen.TAG_NAME)
//                            )
//                    )
            );

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext processingContext, @NotNull CompletionResultSet result) {

        PsiElement psiElement = parameters.getPosition();
        if(! MyDomUtils.isMoquiProject(psiElement.getProject())) return;

        BeginAndEndCharPattern charPattern = BeginAndEndCharPattern.of(psiElement);
        String inputString = MyStringUtils.getDummyFrontString(charPattern.getContent());
        String allString = MyStringUtils.removeDummy(charPattern.getContent());
        boolean inputDotAtEnd = inputString.equals(allString) &&(
                inputString.endsWith(ServiceUtils.SERVICE_NAME_DOT)
        );//只有将dot或#输入在最后一位，才进行特别处理

        Project project = psiElement.getProject();
        LOGGER.warn("in ServiceCallCompletionProvider inputStr："+inputString);

        lookupService(project,inputString,result,inputDotAtEnd);
//        if(MyDomUtils.isMoquiProject(psiElement.getProject())) {
//            LOGGER.warn(parameters.getPosition().getText());

//            result.addAllElements(findCompletionItem(psiElement));
//        }

    }
    private void lookupService(@NotNull Project project, @NotNull String inputStr, @NotNull CompletionResultSet result, boolean inputDotAtEnd){
        List<LookupElement> lookupElementList = new ArrayList<>();
        int hashIndex = inputStr.indexOf(ServiceUtils.SERVICE_NAME_HASH);
        String[] hashSplit = inputStr.split(ServiceUtils.SERVICE_NAME_HASH);

//        CompletionResultSet newResult;
        if(hashIndex >= 0) {
            //#存在，需要进行进一步判断
            int pointIndex = hashSplit[0].lastIndexOf('.');
            if(pointIndex<0) {
                //标准操作,CRUD
                //不是标准操作，则返回空
                if(! ServiceUtils.STANDARD_CRUD_COMMANDER.contains(hashSplit[0])) {
                    return;
                }
                String filterPackageName = MyStringUtils.EMPTY_STRING;
                //是标准操作，则返回entityNameList，需要根据后半部分进行过滤
                if(hashSplit.length==2) {
                    int backPointIndex = hashSplit[1].lastIndexOf('.');
                    filterPackageName = backPointIndex > 0 ? hashSplit[1].substring(0, backPointIndex) : "";


                }
                addEntityLookupElement(project,hashSplit[0]+ServiceUtils.SERVICE_NAME_HASH, filterPackageName,lookupElementList,inputDotAtEnd);

//                newResult = result.withPrefixMatcher(hashSplit[0]+ServiceUtils.SERVICE_NAME_HASH);
            }else{
                //Service Call,如果verb#noun完整,则不处理
                var className = hashSplit[0].substring(0, pointIndex);
                if(hashSplit.length==1) {
                    var verb = hashSplit[0].substring(pointIndex+1);
                    addServiceCallNounLookupElement(project,className, verb,lookupElementList);

                }else {
                    if (!inputDotAtEnd) {
                        addServiceCallVerbNounLookupElement(project, className, lookupElementList);
                    }
                }
//                newResult = result.withPrefixMatcher(className);
            }
        }else {
            //#不存在，也需要进一步判断是否有“.”，没有这这个字符则需要将CRUD操作放入返回列表中
            int pointIndex = inputStr.lastIndexOf('.');
            String filterClassName = MyStringUtils.EMPTY_STRING;
            if(pointIndex<0) {
                //没有，则取所有的className和CRUD
                addStandardCrudLookupElement(project,lookupElementList);
//                newResult = result;
            }else {
                //取类的过滤字符串
                filterClassName= inputStr.substring(0, pointIndex);
//                newResult = result.withPrefixMatcher(filterClassName);

            }
            addServiceCallLookupElement(project,filterClassName,lookupElementList,inputDotAtEnd);

        }

        result.addAllElements(lookupElementList);
    }

    private void addEntityLookupElement(@NotNull Project project,@NotNull String crudString, @NotNull String filterPackageName,@NotNull List<LookupElement> lookupList, @NotNull boolean inputDotAtEnd) {
        EntityUtils.getEntityFullNameSet(project,filterPackageName)
//                .stream()
//                .filter(item->filterPackageName.isEmpty() || item.startsWith(filterPackageName))
//                .map(item->filterPackageName.isEmpty() ? item : item.substring(filterPackageName.length()+1))
//                .map(item->crudString+item)
                .forEach(item->{
                    if(inputDotAtEnd) {
                        String newItem = MyStringUtils.EMPTY_STRING;
                        int filterLength = filterPackageName.length();
                        if(filterLength+1<item.length())
                            if(item.charAt(filterLength) == '.') //必须是完整的名称
                                newItem =item.substring(filterLength+1);

                        if(! newItem.equals(MyStringUtils.EMPTY_STRING)){
                            lookupList.add(LookupElementBuilder.create(newItem)
                                    .withCaseSensitivity(true)
                                    .withIcon(MyIcons.EntityTag)
                                    .withInsertHandler(AutoShowByCharInsertHandler.ofDot())

                            );
                        }
                    }else {
                        lookupList.add(LookupElementBuilder.create(filterPackageName.isEmpty() ? item: crudString+item)
                                        .withCaseSensitivity(true)
                                        .withIcon(MyIcons.EntityTag)
                                        .withInsertHandler(ClearTailInsertHandler.of())

                        );

                    }

                });

    }
    private static void addServiceCallNounLookupElement(@NotNull Project project, @NotNull String filterClassName,@NotNull String verb, @NotNull List<LookupElement> lookupList){
        HashMap<String, Services> servicesMap = ServiceUtils.getServiceClassNameMap(project, filterClassName);
        if (servicesMap.size()==1) {
            //当前className是个完整的名称，则取该class下的所有服务
            ServiceUtils.getServiceActionNounList(servicesMap.get(filterClassName),verb)
                    .forEach((item)-> {
                            lookupList.add(
                                    LookupElementBuilder.create(item)
                                            .withCaseSensitivity(true)
                                            .withIcon(MyIcons.ServiceTag)
                                            .withInsertHandler(ClearTailInsertHandler.of())
                            );
                    });
        }
    }
    private static void addServiceCallVerbNounLookupElement(@NotNull Project project, @NotNull String filterClassName, @NotNull List<LookupElement> lookupList){
        HashMap<String, Services> servicesMap = ServiceUtils.getServiceClassNameMap(project, filterClassName);
        if (servicesMap.size()==1) {
            //当前className是个完整的名称，则取该class下的所有服务
            ServiceUtils.getServiceActionList(servicesMap.get(filterClassName))
                    .forEach((item)-> {
                        lookupList.add(
                                LookupElementBuilder.create(item)
                                        .withCaseSensitivity(true)
                                        .withIcon(MyIcons.ServiceTag)
                                        .withInsertHandler(ClearTailInsertHandler.of())
                        );
                    });
        }
    }


    private static void addServiceCallLookupElement(@NotNull Project project, @NotNull String filterClassName,@NotNull List<LookupElement> lookupList,boolean inputDotAtEnd){
        HashMap<String, Services> servicesMap = ServiceUtils.getServiceClassNameMap(project, filterClassName);
        if (servicesMap.size()==1) {
            //当前className是个完整的名称，则取该class下的所有服务
            ServiceUtils.getServiceActionList(servicesMap.get(filterClassName))
                    .forEach((item)-> {
                        if(inputDotAtEnd){
                            lookupList.add(
                                    LookupElementBuilder.create(item)
                                            .withCaseSensitivity(true)
                                            .withIcon(MyIcons.ServiceTag)
                                            .withInsertHandler(ClearTailInsertHandler.of())
                            );


                        }else {
                            lookupList.add(
                                    LookupElementBuilder.create(filterClassName+ServiceUtils.SERVICE_NAME_DOT+item)
                                            .withCaseSensitivity(true)
                                            .withIcon(MyIcons.ServiceTag)
                                            .withInsertHandler(ClearTailInsertHandler.of())
                            );
                        }
                    });

        }else {
            servicesMap.keySet()
                    .forEach((item)-> {
                        if(inputDotAtEnd) {
                            String newItem = MyStringUtils.EMPTY_STRING;
                            int filterLength = filterClassName.length();
                            if(filterLength+1<item.length())
                                if(item.charAt(filterLength)== '.') //必须是完整的名称
                                    newItem =item.substring(filterLength+1);
                            if(!newItem.equals(MyStringUtils.EMPTY_STRING)) {
                                lookupList.add(
                                        LookupElementBuilder.create(newItem)
                                                .withCaseSensitivity(true)
                                                .withIcon(MyIcons.ServiceTag)
                                                .withInsertHandler(ClearTailInsertHandler.of())

                                );
                            }

                        }else {
                            lookupList.add(
                                    LookupElementBuilder.create(item)
                                            .withCaseSensitivity(true)
                                            .withIcon(MyIcons.ServiceTag)
                                        .withInsertHandler(AutoShowByCharInsertHandler.ofDot())
                            );
                        }
                    });

        }

    }
    private static void addStandardCrudLookupElement(@NotNull Project project, @NotNull List<LookupElement> lookupList){
        ServiceUtils.STANDARD_CRUD_COMMANDER.forEach( item->{
            lookupList.add(LookupElementBuilder.create(item)
                    .withCaseSensitivity(false)
                    .withTypeText("Entity CRUD")
                    .withInsertHandler(AutoShowByCharInsertHandler.ofHash())
            );
        });

    }
//    public abstract List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement);
}
