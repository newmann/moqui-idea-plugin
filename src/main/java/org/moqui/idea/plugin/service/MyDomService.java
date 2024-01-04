package org.moqui.idea.plugin.service;

import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.dom.model.Services;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.List;

public interface MyDomService {

  static MyDomService getInstance() {
    return ApplicationManager.getApplication().getService(MyDomService.class);
  }

  @NotNull
  List<Services> getServicesList(@NotNull PsiClass psiClass,
                                 @Nullable GlobalSearchScope scope);

  @Nullable
  Service getService(@NotNull PsiMethod psiMethod,
                     @Nullable GlobalSearchScope scope);

  @Nullable DomFileElement<Services> getServices(XmlFile xmlFile);

  @NotNull
  List<LookupElementBuilder> getLookupElement(PsiElement psiElement);
}
