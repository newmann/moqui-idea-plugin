package org.moqui.idea.plugin.service;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.xml.ConvertContext;
import org.moqui.idea.plugin.dom.model.AbstractField;

import java.util.List;

/**
 * 为Convert提供服务
 * 根据当前ConvertContext context，找到可供快速输入的AbstractField
 */
@Deprecated
public interface AbstractFieldCompletionService {

  /**
   * 是否支持当前ConvertContext的Completion
   * @param context 当前
   * @return
   */
  boolean isSupport(ConvertContext context);

  /**
   * 根据当前的ConvertContext，找到供快速匹配的内容
   *
   * @return
   */
  List<AbstractField> findCompletionItemList(ConvertContext context);

}
