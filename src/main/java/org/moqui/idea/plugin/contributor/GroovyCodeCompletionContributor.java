package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral;
import org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyElementPattern;
import org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns;
import org.moqui.idea.plugin.provider.EntityOrViewNameCompletionProvider;

import static com.intellij.patterns.PsiJavaPatterns.psiMethod;
import static org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns.groovyLiteralExpression;

public class GroovyCodeCompletionContributor extends CompletionContributor {
    private static Logger LOG = Logger.getInstance(GroovyCodeCompletionContributor.class);

    public static final String ENTITY_FACADE_CLASS = "org.moqui.entity.EntityFacade";
    public static final String ENTITY_IMPL_FACADE_CLASS = "org.moqui.impl.entity.EntityFacadeImpl";

    public static final PsiElementPattern<PsiElement, PsiElementPattern.Capture<PsiElement>> ENTITY_CALL =
            PlatformPatterns.psiElement().inside(GroovyPatterns.psiElement().andOr(
                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("find").definedInClass(ENTITY_FACADE_CLASS)),
                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("makeValue").definedInClass(ENTITY_FACADE_CLASS)),
                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("fastFindOne").definedInClass(ENTITY_FACADE_CLASS)),
                    groovyLiteralExpression().methodCallParameter(2, psiMethod().withName("sqlFind").definedInClass(ENTITY_FACADE_CLASS)),
                    groovyLiteralExpression().methodCallParameter(2, psiMethod().withName("getEntityGroupName").definedInClass(ENTITY_FACADE_CLASS))
            ));
//            GroovyPatterns.groovyLiteralExpression().inside(GroovyPatterns.psiElement().andOr(
//                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("find").definedInClass(ENTITY_FACADE_CLASS)),
//                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("makeValue").definedInClass(ENTITY_FACADE_CLASS)),
//                    groovyLiteralExpression().methodCallParameter(0, psiMethod().withName("fastFindOne").definedInClass(ENTITY_FACADE_CLASS)),
//                    groovyLiteralExpression().methodCallParameter(2, psiMethod().withName("sqlFind").definedInClass(ENTITY_FACADE_CLASS)),
//                    groovyLiteralExpression().methodCallParameter(2, psiMethod().withName("getEntityGroupName").definedInClass(ENTITY_FACADE_CLASS))
//            ));

    public GroovyCodeCompletionContributor() {

        extend(CompletionType.BASIC, ENTITY_CALL, new EntityOrViewNameCompletionProvider());
    }

//    @Override
//    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
//        LOG.warn("fillCompletionVariants: "+ parameters.getPosition().getText());
//        super.fillCompletionVariants(parameters, result);
//
//    }
}
