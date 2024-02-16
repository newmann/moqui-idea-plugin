package org.moqui.idea.plugin.service;

import com.intellij.psi.PsiElement;
import org.moqui.idea.plugin.service.impl.*;

import java.util.ArrayList;
import java.util.List;

public class FindEditCompletionServiceFactory {
  private static final List<EditCompletionService> completionServiceList = new ArrayList<>();

  static {
    completionServiceList.add(EntitiesPackageEditCompletionService.INSTANCE);

  }

  public static EditCompletionService getEditCompletionService(PsiElement psiElement) {
    for (EditCompletionService completionService : completionServiceList) {
      if (completionService.isSupport(psiElement)) {
        return completionService;
      }
    }
    return NotSupportEditCompletionService.INSTANCE;
  }
}
