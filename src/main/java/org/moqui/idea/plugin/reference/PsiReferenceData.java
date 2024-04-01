package org.moqui.idea.plugin.reference;

import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiReference;
@Deprecated
public class PsiReferenceData {
    private Long modificationStamp;
    private PsiReference[] psiReferences;
    PsiReferenceData(Long modificationStamp, PsiReference[] psiReferences) {
        this.psiReferences = psiReferences;
        this.modificationStamp = modificationStamp;
    }

    public static final Key<PsiReferenceData> MOQUI_ENTITY_REFERENCES = Key.create("MOQUI_ENTITY_REFERENCES");
    public static final Key<PsiReferenceData> MOQUI_SERVICE_REFERENCES = Key.create("MOQUI_SERVICE_REFERENCES");
    public static final Key<PsiReferenceData> MOQUI_LOCATION_REFERENCES = Key.create("MOQUI_LOCATION_REFERENCES");
    public Long getModificationStamp() {return modificationStamp;}

    public PsiReference[] getPsiReferences() {
        return psiReferences;
    }

}
