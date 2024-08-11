package org.moqui.idea.plugin.action.flowManagement;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

public class MoquiVertex {
    private final String type;
    private final String name;
    private final PsiElement element;
    MoquiVertex(@NotNull String type,@NotNull String name, @NotNull PsiElement element){
        this.type = type;
        this.name = name;
        this.element = element;
    }
    

}
