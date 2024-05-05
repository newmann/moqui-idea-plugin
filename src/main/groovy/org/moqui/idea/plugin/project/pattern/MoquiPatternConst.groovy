package org.moqui.idea.plugin.project.pattern

import com.intellij.patterns.PsiJavaElementPattern
import com.intellij.patterns.PsiJavaPatterns
import com.intellij.psi.PsiLiteral

class MoquiPatternConst {
    public static final String ENTITY_FACADE_CLASS = 'org.moqui.entity.EntityFacade'

    static Object makeMethodParameterPattern(PsiJavaElementPattern<? extends PsiLiteral,?> elementPattern,
        String methodName, String className, int index){
        return elementPattern.methodCallParameter(index, PsiJavaPatterns.psiMethod().withName(methodName).definedInClass(className))
    }
}
