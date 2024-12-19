package org.moqui.idea.plugin.contributor;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceRegistrar;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns;
import org.moqui.idea.plugin.provider.EntityOrViewNameReferenceProvider;
import org.moqui.idea.plugin.provider.ServiceCallReferenceProvider;
import org.moqui.idea.plugin.util.EntityScope;

import static com.intellij.patterns.PsiJavaPatterns.psiMethod;
import static org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns.groovyLiteralExpression;

public class GroovyCodeReferenceContributor extends PsiReferenceContributor {
    public static final String ENTITY_FACADE_CLASS = "org.moqui.entity.EntityFacade";
    public static final String ENTITY_FIND_CLASS = "org.moqui.entity.EntityFind";
    public static final String ENTITY_IMPL_FACADE_CLASS = "org.moqui.impl.entity.EntityFacadeImpl";
    public static final String SERVICE_CALL_SYNC_CLASS = "org.moqui.service.ServiceCallSync";
    public static final String SERVICE_CALL_ASYNC_CLASS = "org.moqui.service.ServiceCallAsync";
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> ENTITY_FIND_PATTERN =
            PlatformPatterns.psiElement().inside(GroovyPatterns.psiElement().andOr(
                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("find").definedInClass(ENTITY_FACADE_CLASS)),
                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("fastFindOne").definedInClass(ENTITY_FACADE_CLASS)),
                    groovyLiteralExpression().methodCallParameter(2, psiMethod().withName("sqlFind").definedInClass(ENTITY_FACADE_CLASS)),
                    groovyLiteralExpression().methodCallParameter(2, psiMethod().withName("getEntityGroupName").definedInClass(ENTITY_FACADE_CLASS))
            ));
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> ENTITY_UPDATE_PATTERN =
            PlatformPatterns.psiElement().inside(GroovyPatterns.psiElement().andOr(
                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("makeValue").definedInClass(ENTITY_FACADE_CLASS))
            ));
    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> SERVICE_CALL_PATTERN =
            PlatformPatterns.psiElement().inside(GroovyPatterns.psiElement().andOr(
                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("name").definedInClass(SERVICE_CALL_SYNC_CLASS)),
                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("name").definedInClass(SERVICE_CALL_ASYNC_CLASS))
            ));



    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {

        registrar.registerReferenceProvider(SERVICE_CALL_PATTERN, ServiceCallReferenceProvider.of());
        registrar.registerReferenceProvider(ENTITY_FIND_PATTERN, EntityOrViewNameReferenceProvider.of(EntityScope.ENTITY_AND_VIEW));
        registrar.registerReferenceProvider(ENTITY_UPDATE_PATTERN, EntityOrViewNameReferenceProvider.of(EntityScope.ENTITY_ONLY));
//        registrar.registerReferenceProvider(SERVICE_CALL_PATTERN, EntityOrViewNameReferenceProvider.of(EntityScope.ENTITY_ONLY));

    }
}
