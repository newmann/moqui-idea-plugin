package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import org.moqui.idea.plugin.provider.*;

/**
 * 所有针对Attribute的Complete Contributor做法：
 * 1、先定义pattern，定位到attribute value的psiElement
 * 2、再定义Completion Provider
 * 3、在这里注册使用，无需在plugin.xml中注册
 *
 */
public class MoquiAttributeCompletionContributor extends CompletionContributor {

  MoquiAttributeCompletionContributor(){
    extend(CompletionType.BASIC, AliasNameCompletionProvider.ALIAS_NAME_PATTERN,AliasNameCompletionProvider.of());
    extend(CompletionType.BASIC, TransitionIncludeCompletionProvider.TRANSITION_INCLUDE_PATTERN, TransitionIncludeCompletionProvider.of());
    extend(CompletionType.BASIC, SectionIncludeCompletionProvider.SECTION_INCLUDE_PATTERN, SectionIncludeCompletionProvider.of());
    extend(CompletionType.BASIC, TransitionCompletionProvider.TRANSITION_PATTERN, TransitionCompletionProvider.of());
    extend(CompletionType.BASIC, SubScreensDefaultItemCompletionProvider.SUB_SCREENS_DEFAULT_ITEM_PATTERN, SubScreensDefaultItemCompletionProvider.of());
    extend(CompletionType.BASIC, MultiFieldNameCompletionProvider.MULTI_FIELD_NAME_PATTERN, MultiFieldNameCompletionProvider.of());
  }


}
