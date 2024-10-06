package org.moqui.idea.plugin.provider;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.AbstractEntity;
import org.moqui.idea.plugin.reference.AbstractEntityOrViewNameReference;
import org.moqui.idea.plugin.util.BeginAndEndCharPattern;
import org.moqui.idea.plugin.util.EntityScope;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EntityOrViewNameReferenceProvider extends PsiReferenceProvider {
    private static Logger logger = Logger.getInstance(EntityOrViewNameReferenceProvider.class);
    public static EntityOrViewNameReferenceProvider of(@NotNull EntityScope entityScope){
        return new EntityOrViewNameReferenceProvider(entityScope);
    }

    private EntityScope myEntityScope;
    public EntityOrViewNameReferenceProvider(@NotNull EntityScope entityScope) {
        myEntityScope = entityScope;
    }

    @Override
    public @NotNull  PsiReference[] getReferencesByElement(@NotNull PsiElement psiElement, @NotNull ProcessingContext processingContext) {
        return EntityUtils.createEntityOrViewNameReferences(psiElement.getProject(),psiElement,myEntityScope);

//        BeginAndEndCharPattern stringPattern = BeginAndEndCharPattern.of(psiElement);
//        if(stringPattern.getContent().isBlank()){
////            logger.warn("EntityOrViewNameReferenceProvider->getReferencesByElement：is blank");
//            return new PsiReference[0];
//        }else {
////            logger.warn("EntityOrViewNameReferenceProvider->getReferencesByElement：content is "+ stringPattern.getContent());
//            return createEntityNameReferences(psiElement.getProject(), psiElement, stringPattern.getContent(), stringPattern.getBeginChar().length());
//        }
    }

//    private @NotNull PsiReference[] createEntityNameReferences(@NotNull Project project, @NotNull PsiElement element, @NotNull String  entityName, @NotNull int startOffset) {
//        Optional<AbstractEntity> entityOptional = EntityUtils.getEntityOrViewEntityByName(project, entityName);
//
//        List<PsiReference> psiReferences = new ArrayList<>();
//        int lastDotIndex = entityName.lastIndexOf('.');
//
//        if(entityOptional.isPresent()) {
//            if(lastDotIndex >0) {
//
//                //package reference
//                psiReferences.add(createReference(element,
//                        new TextRange(startOffset,lastDotIndex + startOffset),
//                        MyDomUtils.getPsiElementFromAttributeValue(entityOptional.get().getPackage().getXmlAttributeValue()).orElse(null))
//                );
//                //entityname reference
//                psiReferences.add(createReference(element,
//                        new TextRange(startOffset + lastDotIndex + 1,startOffset + entityName.length()),
//                        MyDomUtils.getPsiElementFromAttributeValue(entityOptional.get().getEntityName().getXmlAttributeValue()).orElse(null))
//                );
//            }else {
//                //entityname reference
//                psiReferences.add(createReference(element,
//                        new TextRange(startOffset,startOffset + entityName.length()),
//                        MyDomUtils.getPsiElementFromAttributeValue(entityOptional.get().getEntityName().getXmlAttributeValue()).orElse(null))
//                );
//
//            }
//
//        }else {
//            if (lastDotIndex <= 0) {
//                //没有含包名
//                psiReferences.add(createReference(element,
//                        new TextRange(startOffset,entityName.length() + startOffset),
//                        null));
//
//
//            } else {
//                psiReferences.add(createReference(element,
//                        new TextRange(startOffset + lastDotIndex + 1,entityName.length() + startOffset),
//                        null));
//
//            }
//        }
//        return psiReferences.toArray(new PsiReference[0]);
//    }
//
//    private PsiReference createReference(PsiElement element, TextRange textRange, PsiElement resolve){
//        return switch (myEntityScope) {
//            case ENTITY_ONLY -> AbstractEntityOrViewNameReference.ofEntityNameReference(element, textRange,resolve);
//            case VIEW_ONLY -> AbstractEntityOrViewNameReference.ofViewEntityNameReference(element, textRange,resolve);
//            case ENTITY_AND_VIEW -> AbstractEntityOrViewNameReference.ofEntityAndViewEntityNameReference(element, textRange,resolve);
//        };
//    }
}
