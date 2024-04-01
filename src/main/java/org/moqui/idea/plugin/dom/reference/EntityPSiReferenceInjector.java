package org.moqui.idea.plugin.dom.reference;

import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomReferenceInjector;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityPSiReferenceInjector implements DomReferenceInjector {
    @Override
    public PsiReference @NotNull [] inject(@Nullable @NonNls String unresolvedText, @NotNull PsiElement element, @NotNull ConvertContext context) {
        if (!StringUtil.isEmptyOrSpaces(unresolvedText)) {
            System.out.println("Inject " + unresolvedText + "PsiElement: " + element);
        }
        return new PsiReference[0];
    }

    @Override
    public @Nullable @NlsSafe String resolveString(@Nullable @NonNls String unresolvedText, @NotNull ConvertContext context) {
        if (StringUtil.isEmptyOrSpaces(unresolvedText)) return unresolvedText;
        System.out.println("Resolving " + unresolvedText +",position:"+ context.getXmlElement().getTextRange().toString()+", in file:"+ context.getFile().getVirtualFile().getPath());
        return unresolvedText+" - Resolved";
    }
}
