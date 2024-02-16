package org.moqui.idea.plugin.dom.converter;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Collection;

/**
 * 指向可用的Screen文件
 */
public class ScreenLocationConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter {
    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext context) {

        return ServiceUtils.findInterfaceFullNameSet(context.getProject(), MyStringUtils.EMPTY_STRING);

    }


    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        final String serviceCallStr = value.getStringValue();
        if(MyStringUtils.isEmpty(serviceCallStr)) return PsiReference.EMPTY_ARRAY;

        Project project = context.getProject();
        return ServiceUtils.createServiceCallReferences(project,element,serviceCallStr,1);


    }


}
