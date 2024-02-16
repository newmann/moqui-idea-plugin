package org.moqui.idea.plugin.service.impl;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import org.moqui.idea.plugin.service.EditCompletionService;

import java.util.ArrayList;
import java.util.List;

public class NotSupportEditCompletionService implements EditCompletionService {
  public static NotSupportEditCompletionService INSTANCE = new NotSupportEditCompletionService();

  private NotSupportEditCompletionService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {
    return true;
  }

  @Override
  public List<LookupElementBuilder> findCompletionItem(PsiElement psiElement) {
    return new ArrayList<>();
  }

}
