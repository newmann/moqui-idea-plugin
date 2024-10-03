package org.moqui.idea.plugin.provider;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.reference.*;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.List;

public class ServiceCallReferenceProvider extends PsiReferenceProvider {
    private static Logger logger = Logger.getInstance(ServiceCallReferenceProvider.class);
    public static ServiceCallReferenceProvider of(){
        return new ServiceCallReferenceProvider();
    }
    public ServiceCallReferenceProvider() {
        super();
    }

    @Override
    public @NotNull  PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {

        BeginAndEndCharPattern stringPattern = BeginAndEndCharPattern.of(psiElement);
        List<PsiReference> psiReferences = new ArrayList<>();
        int tmpStartOffset,tmpEndOffset;

        if(stringPattern.getContent().isBlank()){
            tmpStartOffset = stringPattern.getBeginChar().length();
            tmpEndOffset = tmpStartOffset;
            psiReferences.add(ServiceCallReference.of(psiElement,
                    new TextRange(tmpStartOffset,tmpEndOffset),null));//提供 code completion

        }else {
            List<Pair<TextRange,PsiElement>> textRangeList = ServiceUtils.createServiceCallReferences(psiElement.getProject(),psiElement);

            textRangeList.forEach(item->{
                psiReferences.add(ServiceCallReference.of(psiElement,item.first,item.second));
            });

            if (psiReferences.isEmpty()) {

                int lastDotIndex = stringPattern.getContent().lastIndexOf(ServiceUtils.SERVICE_NAME_DOT);
                tmpEndOffset = stringPattern.getBeginChar().length() + stringPattern.getContent().length();

                if(lastDotIndex>=0) {
                    tmpStartOffset = lastDotIndex + stringPattern.getBeginChar().length()+1;
                }else {
                    int hashIndex = stringPattern.getContent().lastIndexOf(ServiceUtils.SERVICE_NAME_HASH);
                    if (hashIndex>=0) {
                        tmpStartOffset = hashIndex+ stringPattern.getBeginChar().length()+1;
                    }else {
                        tmpStartOffset = stringPattern.getBeginChar().length();
                    }
                }

                psiReferences.add(ServiceCallReference.of(psiElement,
                        new TextRange(tmpStartOffset,tmpEndOffset),null));//提供 code completion
            }

        }
        return psiReferences.toArray(new PsiReference[0]);
    }
}
