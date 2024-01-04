package org.moqui.idea.plugin.provider.filter;

import com.intellij.psi.PsiElement;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public class EmptyAbstractElementFilter extends AbstractElementFilter{
    @Override
    protected Collection<? extends DomElement> getResults(@NotNull PsiElement element) {
        return Collections.emptyList();
    }

}
