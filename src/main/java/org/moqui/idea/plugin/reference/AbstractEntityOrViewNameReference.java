// Copyright 2000-2023 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.moqui.idea.plugin.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.moqui.idea.plugin.MyIcons;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.insertHandler.ClearTailInsertHandler;
import org.moqui.idea.plugin.util.*;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AbstractEntityOrViewNameReference extends MoquiBaseReference {
  public static AbstractEntityOrViewNameReference ofEntityNameReference(@NotNull PsiElement element, TextRange textRange, PsiElement resolve){
      return new AbstractEntityOrViewNameReference(element, textRange,EntityScope.ENTITY_ONLY,resolve);
  }

  public  static AbstractEntityOrViewNameReference ofViewEntityNameReference(@NotNull PsiElement element, TextRange textRange, PsiElement resolve){
      return new AbstractEntityOrViewNameReference(element, textRange,EntityScope.VIEW_ONLY,resolve);
  }

  public  static AbstractEntityOrViewNameReference ofEntityAndViewEntityNameReference(@NotNull PsiElement element, TextRange text, PsiElement resolve){
      return new AbstractEntityOrViewNameReference(element, text,EntityScope.ENTITY_AND_VIEW,resolve);
  }

//  private final Logger LOGGER = Logger.getInstance(AbstractEntityOrViewNameReference.class);

    private final EntityScope entityScope;

//  private TextRange myTextRange;

  public AbstractEntityOrViewNameReference(@NotNull PsiElement element, TextRange textRange, EntityScope entityScope, PsiElement resolve) {
    super(element, textRange,resolve);
//    this.myTextRange = textRange;
    this.entityScope = entityScope;

  }
  @Override
  public  @NotNull  Object[] getVariants() {

    BeginAndEndCharPattern charPattern = BeginAndEndCharPattern.of(myElement);
    String inputString = MyStringUtils.getDummyFrontString(charPattern.getContent());

//    boolean isDot = inputString.endsWith(EntityUtils.ENTITY_NAME_DOT);

    int lastDotIndex = inputString.lastIndexOf(MyStringUtils.ENTITY_NAME_DOT);

    if(lastDotIndex>0) {
      inputString = inputString.substring(0,lastDotIndex);
    }else {
      inputString = MyStringUtils.EMPTY_STRING;
    }

    Project project = myElement.getProject();
    List<LookupElement> variants = new ArrayList<>();
    switch (this.entityScope) {
      case ENTITY_ONLY-> addEntityToVariants(project,variants,inputString);
      case VIEW_ONLY-> addViewEntityToVariants(project,variants,inputString);
      case ENTITY_AND_VIEW->{
          addEntityToVariants(project,variants,inputString);
          addViewEntityToVariants(project,variants,inputString);
      }
    }
    return variants.toArray();
  }
  private void addEntityToVariants( Project project, List<LookupElement> variants,String filter){
    processCollection(EntityUtils.getAllEntityFullNameCollection(project),variants,filter, MyIcons.EntityTag);
  }
  private void addViewEntityToVariants( Project project, List<LookupElement> variants,String filter){
    processCollection(EntityUtils.getAllViewEntityFullNameCollection(project),variants,filter, MyIcons.ViewEntityTag);

  }
  private void processCollection(Collection<String> names, List<LookupElement> variants,String filter, Icon icon){
    names.stream().filter(item-> item.length() > filter.length())
            .filter(item -> item.startsWith(filter))
//            .map(item->(filter.isEmpty()) ? item: item.substring(filter.length()+1))
            .forEach(
              item -> MyStringUtils.filterClassStyleString(item, filter).ifPresent(
                    newItem -> variants.add(
                      LookupElementBuilder.create(newItem).withIcon(icon)
                      .withCaseSensitivity(true)
                      .withInsertHandler(ClearTailInsertHandler.of())
                    ))
            );
  }

//  @Override
//  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
//
//    return ElementManipulators.getManipulator(this.myElement).handleContentChange(this.myElement,this.myTextRange,newElementName);
//
////    return super.handleElementRename(newElementName);
//  }
}
