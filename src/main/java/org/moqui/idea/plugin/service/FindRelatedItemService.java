package org.moqui.idea.plugin.service;

import com.intellij.psi.PsiElement;

import javax.swing.*;
import java.util.List;

/**
 * 为LineMarker提供服务
 * 找到当前psiElement对应的psiElement
 */
public interface FindRelatedItemService {

  /**
   * 是否支持当前psiElement的转跳
   * @param psiElement 当前元素
   * @return
   */
  boolean isSupport(PsiElement psiElement);

  /**
   * 根据当前的psiElement，找到可以转跳的对象
   * @param psiElement 当前元素
   * @return
   */
  List<PsiElement> findRelatedItem(PsiElement psiElement);

  Icon getNagavitorToIcon();

  String getToolTips();
}
