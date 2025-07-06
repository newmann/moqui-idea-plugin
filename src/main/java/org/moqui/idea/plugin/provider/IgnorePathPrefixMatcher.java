package org.moqui.idea.plugin.provider;

import com.intellij.codeInsight.completion.PlainPrefixMatcher;

public class IgnorePathPrefixMatcher extends PlainPrefixMatcher {
    public IgnorePathPrefixMatcher(String prefix) {
        super(extractEffectivePrefix(prefix));
    }

    private static String extractEffectivePrefix(String prefix) {
        int lastSlashIndex = prefix.lastIndexOf('/');
        if (lastSlashIndex != -1) {
            return prefix.substring(lastSlashIndex + 1);
        }
        return prefix;
    }
}
