package org.moqui.idea.plugin.service;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.highlighting.DomElementAnnotationHolder;
import com.intellij.util.xml.highlighting.DomHighlightingHelper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * 为Inspection提供服务
 *
 */
@Deprecated
public interface InspectDomElementService {

  /**
   * 核查
   * 如果发现有问题，返回值为true
   * @param
   * @return
   */
  boolean foundException(@NotNull DomElement element, @NotNull DomElementAnnotationHolder holder, @NotNull DomHighlightingHelper helper);

}
