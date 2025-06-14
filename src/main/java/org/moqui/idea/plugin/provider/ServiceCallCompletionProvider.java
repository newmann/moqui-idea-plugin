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
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.insertHandler.AutoShowByCharInsertHandler;
import org.moqui.idea.plugin.insertHandler.ClearTailInsertHandler;
import org.moqui.idea.plugin.util.*;

import java.util.HashMap;

public class ServiceCallCompletionProvider extends CompletionProvider<CompletionParameters> {
//    private final  static Logger LOGGER = Logger.getInstance(ServiceCallCompletionProvider.class);
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
//        char inputChar = inputString.charAt(inputString.length()-1);

        String allString = MyStringUtils.removeDummy(charPattern.getContent());
        boolean inputAtEnd = inputString.equals(allString);//只有将dot或#输入在最后一位，才进行特别处理

        Project project = psiElement.getProject();
//        LOGGER.warn("in ServiceCallCompletionProvider inputStr："+inputString);

        lookupService(project,inputString,inputAtEnd, result);
//        if(MyDomUtils.isMoquiProject(psiElement.getProject())) {
//            LOGGER.warn(parameters.getPosition().getText());

//            result.addAllElements(findCompletionItem(psiElement));
//        }

    }
    private void lookupService(@NotNull Project project, @NotNull String inputStr, boolean inputAtEnd, @NotNull CompletionResultSet result){
        int hashIndex = inputStr.indexOf(ServiceUtils.SERVICE_NAME_HASH);
        String[] hashSplit = inputStr.split(ServiceUtils.SERVICE_NAME_HASH);

        if(hashIndex >= 0) {
            //#存在，需要进行进一步判断
            int lastPointIndex = hashSplit[0].lastIndexOf('.');
            if(lastPointIndex<0) {
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
                addEntityLookupElement(project,hashSplit[0]+ServiceUtils.SERVICE_NAME_HASH, filterPackageName,inputAtEnd,result);

            }else{
                //Service Call,如果verb#noun完整,则不处理
                var className = hashSplit[0].substring(0, lastPointIndex);
                var verb = hashSplit[0].substring(lastPointIndex+1);
                addServiceCallNounLookupElement(project,className, verb,inputAtEnd,result);

//                if(hashSplit.length==1) {//说明输入字符为#
//                    var verb = hashSplit[0].substring(lastPointIndex+1);
//                    addServiceCallNounLookupElement(project,className, verb,inputAtEnd,result);
//
//                }else if(hashSplit.length == 2) { //只处理一个#的情况，如果有多个#，说明不符合规范，则不处理
//                    if (!inputAtEnd) {
//                        addServiceCallVerbNounLookupElement(project, className, result);
//                    }
//                }
            }
        }else {
            //#不存在，也需要进一步判断是否有“.”，没有这这个字符则需要将CRUD操作放入返回列表中
            int pointIndex = inputStr.lastIndexOf('.');
            String filterClassName = MyStringUtils.EMPTY_STRING;
            if(pointIndex<0) {
                //没有，则取所有的className和CRUD
                addStandardCrudLookupElement(result);
            }else {
                //取类的过滤字符串
                filterClassName= inputStr.substring(0, pointIndex);


            }
            addServiceCallLookupElement(project,filterClassName,inputAtEnd,result);

        }

    }

    private void addEntityLookupElement(@NotNull Project project,@NotNull String crudString, @NotNull String filterPackageName, boolean inputAtEnd,@NotNull CompletionResultSet result) {
        EntityUtils.getEntityFullNameSet(project,filterPackageName)
                .forEach(item->{
                    if(inputAtEnd) {
//                        String newItem = MyStringUtils.EMPTY_STRING;
//                        int filterLength = filterPackageName.length();
//                        if(filterLength == 0) {
//                            newItem = item;
//                        }else {
//                            if (filterLength + 1 < item.length())
//                                if (item.charAt(filterLength) == '.') //必须是完整的名称
//                                    newItem = item.substring(filterLength + 1);
//                        }
//                        if(! newItem.equals(MyStringUtils.EMPTY_STRING)){
//                            result.addElement(LookupElementBuilder.create(newItem)
//                                    .withCaseSensitivity(true)
//                                    .withIcon(MyIcons.EntityTag)
//                                    .withInsertHandler(ClearTailInsertHandler.of())
//
//                            );
//                        }
//
                        MyStringUtils.filterClassStyleString(item,filterPackageName).ifPresent(
                                newItem -> result.addElement(LookupElementBuilder.create(newItem)
                                        .withCaseSensitivity(true)
                                        .withIcon(MyIcons.EntityTag)
                                        .withInsertHandler(ClearTailInsertHandler.of())

                                )
                        );
                    }else {
                        result.addElement(LookupElementBuilder.create(crudString+item)
                                        .withCaseSensitivity(true)
                                        .withIcon(MyIcons.EntityTag)
                                        .withInsertHandler(ClearTailInsertHandler.of())

                        );

                    }

                });

    }
    private static void addServiceCallNounLookupElement(@NotNull Project project, @NotNull String filterClassName,@NotNull String verb, boolean inputAtEnd, @NotNull CompletionResultSet result){
        HashMap<String, Services> servicesMap = ServiceUtils.getServiceClassNameMap(project, filterClassName);
        if (servicesMap.size()==1) {
            //当前className是个完整的名称，则取该class下的所有服务
            ServiceUtils.getServiceActionNounList(servicesMap.get(filterClassName),verb)
                    .forEach((item)-> {
                        if(inputAtEnd) {
                            result.addElement(
                                    LookupElementBuilder.create(item)
                                            .withCaseSensitivity(true)
                                            .withIcon(MyIcons.ServiceTag)
                                            .withInsertHandler(ClearTailInsertHandler.of())
                            );
                        }else {
                            result.addElement(
                                    LookupElementBuilder.create(verb + ServiceUtils.SERVICE_NAME_HASH + item)
                                            .withCaseSensitivity(true)
                                            .withIcon(MyIcons.ServiceTag)
                                            .withInsertHandler(ClearTailInsertHandler.of())
                            );

                        }
                    });
        }
    }

    private static void addServiceCallLookupElement(@NotNull Project project, @NotNull String filterClassName,boolean inputAtEnd,@NotNull CompletionResultSet result){
        HashMap<String, Services> servicesMap = ServiceUtils.getServiceClassNameMap(project, filterClassName);
        if (servicesMap.size()==1) {
            //当前className是个完整的名称，则取该class下的所有服务
            ServiceUtils.getServiceActionList(servicesMap.get(filterClassName))
                    .forEach((item)-> {
                        if(inputAtEnd){
                            result.addElement(
                                    LookupElementBuilder.create(item)
                                            .withCaseSensitivity(true)
                                            .withIcon(MyIcons.ServiceTag)
                                            .withInsertHandler(ClearTailInsertHandler.of())
                            );


                        }else {
                            result.addElement(
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
                        if(inputAtEnd) {
//                            String newItem = MyStringUtils.EMPTY_STRING;
//                            int filterLength = filterClassName.length();
//                            if(filterLength == 0) {
//                                newItem = item;
//                            }else {
//                                if (filterLength + 1 < item.length())
//                                    if (item.charAt(filterLength) == '.') //必须是完整的名称
//                                        newItem = item.substring(filterLength + 1);
//                            }
//                            if(!newItem.equals(MyStringUtils.EMPTY_STRING)) {
//                                result.addElement(
//                                        LookupElementBuilder.create(newItem)
//                                                .withCaseSensitivity(true)
//                                                .withIcon(MyIcons.ServiceTag)
//                                                .withInsertHandler(AutoShowByCharInsertHandler.ofDot())
//
//                                );
//                            }
                            MyStringUtils.filterClassStyleString(item,filterClassName).ifPresent(
                                    newItem-> result.addElement(
                                            LookupElementBuilder.create(newItem)
                                                    .withCaseSensitivity(true)
                                                    .withIcon(MyIcons.ServiceTag)
                                                    .withInsertHandler(AutoShowByCharInsertHandler.ofDot())
                                    )
                            );

                        }else {
                            result.addElement(
                                    LookupElementBuilder.create(item)
                                            .withCaseSensitivity(true)
                                            .withIcon(MyIcons.ServiceTag)
                                        .withInsertHandler(AutoShowByCharInsertHandler.ofDot())
                            );
                        }
                    });

        }

    }
    private static void addStandardCrudLookupElement(@NotNull CompletionResultSet result){

        ServiceUtils.STANDARD_CRUD_COMMANDER.forEach( item-> result.addElement(LookupElementBuilder.create(item)
                .withCaseSensitivity(false)
                .withTypeText("Entity CRUD")
                .withInsertHandler(AutoShowByCharInsertHandler.ofHash())
        ));

    }
//    public abstract List<LookupElementBuilder> findCompletionItem(@NotNull PsiElement psiElement);
}
