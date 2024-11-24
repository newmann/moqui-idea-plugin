package org.moqui.idea.plugin.service.impl;

import com.intellij.psi.PsiElement;
import org.moqui.idea.plugin.service.FindRelatedItemService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class NotSupportFindRelatedItemService implements FindRelatedItemService {
  public static NotSupportFindRelatedItemService INSTANCE = new NotSupportFindRelatedItemService();

  private NotSupportFindRelatedItemService() {
  }

  @Override
  public boolean isSupport(PsiElement psiElement) {
    return true;
  }

  @Override
  public List<PsiElement> findRelatedItem(PsiElement psiElement) {
    return new ArrayList<>();
  }

  @Override
  public Icon getNagavitorToIcon() {
    return null;
  }

  @Override
  public String getToolTips() {
    return null;
  }
}
