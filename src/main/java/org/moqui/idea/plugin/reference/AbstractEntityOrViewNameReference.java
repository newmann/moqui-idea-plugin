// Copyright 2000-2023 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.moqui.idea.plugin.reference;

import com.intellij.openapi.diagnostic.Logger;
import icons.MoquiIcons;
import org.jetbrains.uast.UContinueExpression;
import org.moqui.idea.plugin.dom.model.AbstractEntity;
import org.moqui.idea.plugin.util.BeginAndEndCharPattern;
import org.moqui.idea.plugin.util.EntityScope;
import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AbstractEntityOrViewNameReference extends PsiReferenceBase<PsiElement> {
  public static AbstractEntityOrViewNameReference ofEntityNameReference(@NotNull PsiElement element, TextRange textRange, PsiElement resolve){
      return new AbstractEntityOrViewNameReference(element, textRange,EntityScope.ENTITY_ONLY,resolve);
  }

  public  static AbstractEntityOrViewNameReference ofViewEntityNameReference(@NotNull PsiElement element, TextRange textRange, PsiElement resolve){
      return new AbstractEntityOrViewNameReference(element, textRange,EntityScope.VIEW_ONLY,resolve);
  }

  public  static AbstractEntityOrViewNameReference ofEntityAndViewEntityNameReference(@NotNull PsiElement element, TextRange text, PsiElement resolve){
      return new AbstractEntityOrViewNameReference(element, text,EntityScope.ENTITY_AND_VIEW,resolve);
  }

  private final Logger LOG = Logger.getInstance(AbstractEntityOrViewNameReference.class);
//  private final Optional<AbstractEntity> entityOptional;
    private final String rangeString;
    private final EntityScope entityScope;
    private final PsiElement myResolve;
//  private final EntityUtils.EntityDescriptor entityDescriptor;


  public AbstractEntityOrViewNameReference(@NotNull PsiElement element, TextRange textRange, EntityScope entityScope, PsiElement resolve) {
    super(element, textRange);
      /**
       * 包含包名的实体名
       */
      String entityName = getEntityName(element);
    this.rangeString = textRange.substring(element.getText());
    this.entityScope = entityScope;
    this.myResolve = resolve;
//    this.entityOptional = EntityUtils.getEntityOrViewEntityByName(element.getProject(), entityName);

//    entityDescriptor = EntityUtils.getEntityDescriptorFromFullName(entityName);
  }

  private String getEntityName(PsiElement element){
    BeginAndEndCharPattern stringPattern = BeginAndEndCharPattern.of(element);
//    String name = element.getText();
//    if(name.charAt(0) == '"' || name.charAt(0)=='\'') name = name.substring(1);
//    if(name.endsWith("\"")|| name.endsWith("'")) name = name.substring(0,name.length()-1);
    return stringPattern.getContent();
  }


//  public  @NotNull ResolveResult[] multiResolve(boolean incompleteCode) {
//    List<ResolveResult> resultArray = new ArrayList<>();
//    if(entityOptional.isPresent()) {
//
//      if(rangeString.equals(entityOptional.get().getEntityName().getValue())) { //entity name matched
//        if(entityOptional.get().getEntityName().getXmlAttributeValue()!=null) {
//          resultArray.add(new PsiElementResolveResult(entityOptional.get().getEntityName().getXmlAttributeValue().getOriginalElement()));
//        }
//      }else { //package name matched
//        if(entityOptional.get().getPackage().getXmlAttributeValue()!=null) {
//          resultArray.add(new PsiElementResolveResult(entityOptional.get().getPackage().getXmlAttributeValue().getOriginalElement()));
//        }
//      }
//    }
//    return resultArray.toArray(new ResolveResult[0]);
//  }

  @Nullable
  @Override
  public PsiElement resolve() {
    return myResolve;
//    ResolveResult[] resolveResults = multiResolve(false);
//    return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
  }

  @Override
  public  @NotNull  Object[] getVariants() {

    BeginAndEndCharPattern charPattern = BeginAndEndCharPattern.of(myElement);
    String inputString = MyStringUtils.getDummyFrontString(charPattern.getContent());

//    boolean isDot = inputString.endsWith(EntityUtils.ENTITY_NAME_DOT);

    int lastDotIndex = inputString.lastIndexOf(EntityUtils.ENTITY_NAME_DOT);

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
    processCollection(EntityUtils.getAllEntityFullNameCollection(project),variants,filter,MoquiIcons.EntityTag);
  }
  private void addViewEntityToVariants( Project project, List<LookupElement> variants,String filter){
    processCollection(EntityUtils.getAllViewEntityFullNameCollection(project),variants,filter,MoquiIcons.ViewEntityTag);

  }
  private void processCollection(Collection<String> names, List<LookupElement> variants,String filter, Icon icon){
    names.stream().filter(item-> filter.isEmpty() || item.startsWith(filter))
            .map(item->filter.isEmpty() ? item: item.substring(filter.length()+1))
            .forEach(
            item -> {variants.add(LookupElementBuilder
                        .create(item).withIcon(icon)
                        .withCaseSensitivity(true)
                );
            }
    );

  }
}
