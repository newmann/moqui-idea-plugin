package org.moqui.idea.plugin.service;

import org.moqui.idea.plugin.service.impl.*;
import com.intellij.psi.PsiElement;
import org.moqui.idea.plugin.service.impl.*;


import java.util.ArrayList;
import java.util.List;

public class FindRelatedItemServiceFactory {
  private static final List<FindRelatedItemService> findRelatedItemServiceList = new ArrayList<>();

  static {
    findRelatedItemServiceList.add(ExtendEntityTagFindRelatedItemService.INSTANCE);
    findRelatedItemServiceList.add(MemberEntityTagFindRelatedItemService.INSTANCE);
    findRelatedItemServiceList.add(ServiceCallTagFindRelatedItemService.INSTANCE);
    findRelatedItemServiceList.add(EntityNameAttributeFindRelatedItemService.INSTANCE);

  }

  public static FindRelatedItemService getFindRelatedItemService(PsiElement psiElement) {
    for (FindRelatedItemService findRelatedItemService : findRelatedItemServiceList) {
      if (findRelatedItemService.isSupport(psiElement)) {
        return findRelatedItemService;
      }
    }
    return NotSupportFindRelatedItemService.INSTANCE;
  }
}
