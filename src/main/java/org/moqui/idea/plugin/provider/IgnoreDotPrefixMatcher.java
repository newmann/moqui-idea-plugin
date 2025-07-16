package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.PlainPrefixMatcher;

public class IgnoreDotPrefixMatcher extends PlainPrefixMatcher {
    public IgnoreDotPrefixMatcher(String prefix) {
        super(extractEffectivePrefix(prefix));
    }
    private static String extractEffectivePrefix(String prefix) {
        int lastSlashIndex = prefix.lastIndexOf('.');
        if (lastSlashIndex != -1) {
            return prefix.substring(lastSlashIndex + 1);
        }
        return prefix;
    }
}
