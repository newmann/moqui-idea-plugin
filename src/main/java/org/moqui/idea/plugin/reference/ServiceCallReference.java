// Copyright 2000-2023 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.moqui.idea.plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import icons.MoquiIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.util.*;

import java.util.*;

public class ServiceCallReference extends PsiReferenceBase<PsiElement> {
  public static ServiceCallReference of(@NotNull PsiElement element, TextRange textRange, PsiElement resolveElement){
    return new ServiceCallReference(element, textRange,resolveElement);
  }

  private final Logger logger = Logger.getInstance(ServiceCallReference.class);

    private PsiElement myResolve;

  public ServiceCallReference(@NotNull PsiElement element, TextRange textRange,PsiElement resolveElement) {
    super(element, textRange);
    myResolve = resolveElement;
  }


  @Nullable
  @Override
  public PsiElement resolve() {
    return myResolve;
  }

  @Override
  public  @NotNull  Object[] getVariants() {

    BeginAndEndCharPattern charPattern = BeginAndEndCharPattern.of(myElement);
    String inputString = MyStringUtils.getDummyFrontString(charPattern.getContent());
    Project project = myElement.getProject();
//    logger.warn("inputStr："+inputString);

    return lookupService(project,inputString).toArray();

  }
  private List<LookupElement> lookupService(@NotNull Project project, @NotNull String inputStr){
    List<LookupElement> result = new ArrayList<>();
    int hashIndex = inputStr.indexOf(ServiceUtils.SERVICE_NAME_HASH);
    String[] hashSplit = inputStr.split(ServiceUtils.SERVICE_NAME_HASH);
    String lastChar = inputStr.substring(inputStr.length()-1);

    if(hashIndex >= 0) {
      //#存在，需要进行进一步判断
      int pointIndex = hashSplit[0].lastIndexOf('.');
      if(pointIndex<0) {
        //标准操作,CRUD
        //不是标准操作，则返回空
        if(! ServiceUtils.STANDARD_CRUD_COMMANDER.contains(hashSplit[0])) return result;
        String filterPackageName = MyStringUtils.EMPTY_STRING;
        //是标准操作，则返回entityNameList，需要根据后半部分进行过滤
        if(hashSplit.length==2) {
          int backPointIndex = hashSplit[1].lastIndexOf('.');
          filterPackageName = backPointIndex > 0 ? hashSplit[1].substring(0, backPointIndex) : "";


        }
        addEntityLookupElement(project,filterPackageName,result);
      }else{
        //Service Call
        var className= hashSplit[0].substring(0, pointIndex);
        addServiceCallLookupElement(project,className,result);

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
      addServiceCallLookupElement(project,filterClassName,result);

    }
    return result;
  }

  private void addEntityLookupElement(@NotNull Project project, @NotNull String filterPackageName,@NotNull List<LookupElement> lookupList) {
    EntityUtils.getEntityFullNameSet(project,filterPackageName).stream()
            .filter(item->filterPackageName.isEmpty() || item.startsWith(filterPackageName))
            .map(item->filterPackageName.isEmpty() ? item : item.substring(filterPackageName.length()+1))
            .forEach(item->{
              lookupList.add(LookupElementBuilder.create(item)
                      .withCaseSensitivity(true)
                      .withIcon(MoquiIcons.EntityTag)
              );

            });

  }
  private void addStandardCrudLookupElement(@NotNull List<LookupElement> lookupList){
    ServiceUtils.STANDARD_CRUD_COMMANDER.forEach( item->{
      lookupList.add(LookupElementBuilder.create(item+ServiceUtils.SERVICE_NAME_HASH).withCaseSensitivity(false).withTypeText("Entity CRUD"));
    });

  }
  private void addServiceCallLookupElement(@NotNull Project project, @NotNull String filterClassName,@NotNull List<LookupElement> lookupList){
    Set<String> result = new HashSet<>(ServiceUtils.getServiceClassNameSet(project, filterClassName));
    if (result.size()==1) {
      //当前className是个完整的名称，则取该class下的所有服务
      result.clear();
      result.addAll(ServiceUtils.getServiceAction(project,filterClassName));
      result.forEach((item)-> {
                lookupList.add(
                        LookupElementBuilder.create(item)
                        .withCaseSensitivity(true)
                        .withIcon(MoquiIcons.ServiceTag)
                );
              });

    }else {
      result.stream().filter(item->filterClassName.isEmpty() || item.startsWith(filterClassName))
              .map(item->filterClassName.isEmpty() ? item : item.substring(filterClassName.length()+1))
              .forEach((item)-> {
                lookupList.add(
                        LookupElementBuilder.create(item)
                                .withCaseSensitivity(true)
                                .withIcon(MoquiIcons.ServiceTag)
                );
              });

    }

  }

}
