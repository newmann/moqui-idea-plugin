package org.moqui.idea.plugin.project.pattern

import com.intellij.patterns.PsiElementPattern
import com.intellij.patterns.PsiJavaElementPattern
import com.intellij.patterns.PsiJavaPatterns
import com.intellij.psi.PsiLiteralExpression
import static org.moqui.idea.plugin.project.pattern.MoquiPatternConst.ENTITY_FACADE_CLASS
import static org.moqui.idea.plugin.project.pattern.MoquiPatternConst.makeMethodParameterPattern
import static com.intellij.patterns.PlatformPatterns.psiElement

class MoquiJavaPatterns {
    public static final PsiElementPattern ENTITY_CALL = psiElement().andOr(
            makeEntityFacadeJavaMethodParameterPattern('find',0)

    )

    static PsiJavaElementPattern.Capture<PsiLiteralExpression> makeJavaMethodParameterPattern(String methodName, String className, int index) {
        return makeMethodParameterPattern(PsiJavaPatterns::literalExpression(), methodName, className, index)
    }
    static PsiJavaElementPattern.Capture<PsiLiteralExpression> makeEntityFacadeJavaMethodParameterPattern(String methodName, int index) {
        return makeJavaMethodParameterPattern(methodName,ENTITY_FACADE_CLASS, index)
    }
}
