package org.moqui.idea.plugin.reference;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiReference;

import java.util.List;

public class PsiReferenceData {
    private Long modificationStamp;
    private List<PsiReference> psiReferences;
    PsiReferenceData(Long modificationStamp, List<PsiReference> psiReferences) {
        this.psiReferences = psiReferences;
        this.modificationStamp = modificationStamp;
    }

    public static final Key<PsiReferenceData> ENTITY_REFERENCES = Key.create("ENTITY_REFERENCES");

    public Long getModificationStamp() {return modificationStamp;}

    public List<PsiReference> getPsiReferences() {
        return psiReferences;
    }

}
