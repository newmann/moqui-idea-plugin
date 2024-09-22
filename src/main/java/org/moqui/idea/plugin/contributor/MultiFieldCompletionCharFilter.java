package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.lookup.CharFilter;
import com.intellij.codeInsight.lookup.Lookup;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.FileReference;
import org.moqui.idea.plugin.util.ScreenUtils;

/**
 * 看来和自动弹出completion没有关系
 */
public class MultiFieldCompletionCharFilter extends CharFilter {
    @Override
    public Result acceptChar(char c, int prefixLength, Lookup lookup) {
        final LookupElement item = lookup.getCurrentItem();
        if (',' == c && item != null) {
            if (ScreenUtils.isScreenFile(lookup.getPsiFile())) {
                return Result.ADD_TO_PREFIX;
            }
        }

        return null;
    }
}
