package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import org.moqui.idea.plugin.provider.GroovyFieldNameCompletionProvider;

/**
 * 所有针对Attribute的Complete Contributor做法：
 * 1、先定义pattern，定位到attribute value的psiElement
 * 2、再定义Completion Provider
 * 3、在这里注册使用，无需在plugin.xml中注册
 *
 */
public class GroovyCodeCompletionContributor extends CompletionContributor {

  GroovyCodeCompletionContributor(){
    extend(CompletionType.BASIC, GroovyFieldNameCompletionProvider.CONDITION_PATTERN,GroovyFieldNameCompletionProvider.of());

  }


}