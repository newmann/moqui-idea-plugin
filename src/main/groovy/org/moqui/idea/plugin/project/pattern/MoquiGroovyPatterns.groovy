package org.moqui.idea.plugin.project.pattern

import com.intellij.patterns.PsiElementPattern
import com.intellij.patterns.StandardPatterns
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyPatterns

import static com.intellij.patterns.PlatformPatterns.psiElement
import static org.jetbrains.plugins.groovy.lang.psi.patterns.GroovyElementPattern.Capture
import static org.moqui.idea.plugin.project.pattern.MoquiPatternConst.*

class MoquiGroovyPatterns {
    //============================================
    //       PATTERNS
    //============================================
//    public static final PsiElementPattern ENTITY_CALL = psiElement().inside(psiElement().andOr(
//
//    )
//
//    )
    public static final PsiElementPattern ENTITY_CALL =
//            psiElement().inside(psiElement().andOr(
            GroovyPatterns.stringLiteral().withParent(
                    StandardPatterns.or(
                        GroovyPatterns.psiMethod().withName("find").definedInClass(MoquiPatternConst.ENTITY_FACADE_CLASS),
                        GroovyPatterns.psiMethod().withName("find").definedInClass(MoquiPatternConst.ENTITY_IMPL_FACADE_CLASS)
                    )
            )
//            )
//            psiElement().inside(psiElement().andOr(
//            makeEntityFacadeGroovyMethodParameterPattern( 'find', 0),
//            makeDelegatorGroovyMethodParameterPattern('findOne', 0),
//            makeDelegatorGroovyMethodParameterPattern('findAll', 0),
//            makeDelegatorGroovyMethodParameterPattern('findCountByCondition', 0),
//            makeDelegatorGroovyMethodParameterPattern('findList', 0),
//            makeDynamicViewEntityGroovyMethodParameterPattern('addMemberEntity', 1),
//            makeEntityDataServicesGroovyParameterPattern('makeValue', 1),
//            groovyLiteralExpression().methodCallParameter(0, psiMethod().withName('find')),
//            groovyLiteralExpression().methodCallParameter(0, psiMethod().withName('from')),
//            groovyLiteralExpression().methodCallParameter(0, psiMethod().withName('makeValue')),
//            groovyLiteralExpression().methodCallParameter(0, psiMethod().withName('findOne'))
//    ))

    //============================================
    //       UTILITY METHODS
    //============================================
    static Capture<GrLiteral> makeGroovyMethodParameterPattern(String methodName, String className, int index) {
        return makeMethodParameterPattern(GroovyPatterns::groovyLiteralExpression(), methodName, className, index)
    }


    static Capture<GrLiteral> makeEntityFacadeGroovyMethodParameterPattern(String methodName, int index) {
        return makeGroovyMethodParameterPattern(methodName, ENTITY_FACADE_CLASS, index)
    }
    static Capture<GrLiteral> makeEntityFacadeImplGroovyMethodParameterPattern(String methodName, int index) {
        return makeGroovyMethodParameterPattern(methodName, ENTITY_IMPL_FACADE_CLASS, index)
    }

}
