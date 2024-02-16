package org.moqui.idea.plugin.service;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * 为Completion提供服务
 * 找到当前psiElement可供快速输入匹配的内容
 */
public interface EditCompletionService {

  /**
   * 是否支持当前psiElement的Completion
   * @param psiElement 当前元素
   * @return
   */
  boolean isSupport(PsiElement psiElement);

  /**
   * 根据当前的psiElement，找到供快速匹配的内容
   * @param psiElement 当前元素
   * @return
   */
  List<LookupElementBuilder> findCompletionItem(PsiElement psiElement);

}
