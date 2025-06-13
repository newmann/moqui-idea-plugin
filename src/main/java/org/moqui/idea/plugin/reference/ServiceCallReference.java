// Copyright 2000-2023 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.moqui.idea.plugin.reference;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.moqui.idea.plugin.MyIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.BeginAndEndCharPattern;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServiceCallReference extends MoquiBaseReference {
  public static ServiceCallReference of(@NotNull PsiElement element, TextRange textRange, PsiElement resolveElement){
    return new ServiceCallReference(element, textRange,resolveElement);
  }

  private final Logger logger = Logger.getInstance(ServiceCallReference.class);

//  private TextRange myTextRange;

    private PsiElement myResolveElement;

  public ServiceCallReference(@NotNull PsiElement element, TextRange textRange,PsiElement resolveElement) {
    super(element, textRange,resolveElement);
//    this.myTextRange = textRange;

//    myResolveElement = resolveElement;
  }

//
//  @Nullable
//  @Override
//  public PsiElement resolve() {
//    return myResolve;
//  }
//
//  @Override
//  public  @NotNull  Object[] getVariants() {
//
//    BeginAndEndCharPattern charPattern = BeginAndEndCharPattern.of(myElement);
//    String inputString = MyStringUtils.getDummyFrontString(charPattern.getContent());
//    Project project = myElement.getProject();
//    logger.warn("inputStr："+inputString);
//
//    return lookupService(project,inputString).toArray();
//
//  }
//  private List<LookupElement> lookupService(@NotNull Project project, @NotNull String inputStr){
//    List<LookupElement> result = new ArrayList<>();
//    int hashIndex = inputStr.indexOf(ServiceUtils.SERVICE_NAME_HASH);
//    String[] hashSplit = inputStr.split(ServiceUtils.SERVICE_NAME_HASH);
//
//
//    if(hashIndex >= 0) {
//      //#存在，需要进行进一步判断
//      int pointIndex = hashSplit[0].lastIndexOf('.');
//      if(pointIndex<0) {
//        //标准操作,CRUD
//        //不是标准操作，则返回空
//        if(! ServiceUtils.STANDARD_CRUD_COMMANDER.contains(hashSplit[0])) return result;
//        String filterPackageName = MyStringUtils.EMPTY_STRING;
//        //是标准操作，则返回entityNameList，需要根据后半部分进行过滤
//        if(hashSplit.length==2) {
//          int backPointIndex = hashSplit[1].lastIndexOf('.');
//          filterPackageName = backPointIndex > 0 ? hashSplit[1].substring(0, backPointIndex) : "";
//
//
//        }
//        addEntityLookupElement(project,filterPackageName,result);
//      }else{
//        //Service Call
//        var className= hashSplit[0].substring(0, pointIndex);
//        ServiceUtils.addServiceCallLookupElement(project,className,result);
//
//      }
//    }else {
//      //#不存在，也需要进一步判断是否有“.”，没有这这个字符则需要将CRUD操作放入返回列表中
//      int pointIndex = inputStr.lastIndexOf('.');
//      String filterClassName = MyStringUtils.EMPTY_STRING;
//      if(pointIndex<0) {
//        //没有，则取所有的className和CRUD
//        ServiceUtils.addStandardCrudLookupElement(result);
//      }else {
//        //取类的过滤字符串
//        filterClassName= inputStr.substring(0, pointIndex);
//
//      }
//      ServiceUtils.addServiceCallLookupElement(project,filterClassName,result);
//
//    }
//    return result;
//  }
//
//  private void addEntityLookupElement(@NotNull Project project, @NotNull String filterPackageName,@NotNull List<LookupElement> lookupList) {
//    EntityUtils.getEntityFullNameSet(project,filterPackageName).stream()
//            .filter(item->filterPackageName.isEmpty() || item.startsWith(filterPackageName))
//            .map(item->filterPackageName.isEmpty() ? item : item.substring(filterPackageName.length()+1))
//            .forEach(item->{
//              lookupList.add(LookupElementBuilder.create(item)
//                      .withCaseSensitivity(true)
//                      .withIcon(MyIcons.EntityTag)
//              );
//
//            });
//
//  }

  @Override
  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    String newName = newElementName;
    //如果指向了PsiFile，则需要将.xml后缀去掉
    if(myResolve instanceof PsiFile) {
      int index = newElementName.lastIndexOf(".xml");
      if(index>0){
        newName = newElementName.substring(0,index);
      }
    }
    return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newName);

//    return super.handleElementRename(newElementName);
  }
}
