package org.moqui.idea.plugin.dom.converter;

import com.intellij.codeInspection.util.InspectionMessage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Service;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.intellij.codeInsight.completion.CompletionUtil.DUMMY_IDENTIFIER;
import static org.moqui.idea.plugin.util.ServiceUtils.findServiceByFullName;

public class InterfaceConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter {
    @Override
    public String fromString(String s, ConvertContext context) {
        if(s == null) return null;

        Optional<Service> optService = findServiceByFullName(context.getProject(),s);
        if (optService.isEmpty()) return null;
        return s;
    }

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

    @Override
    public @InspectionMessage String getErrorMessage(@Nullable String s, ConvertContext context) {
        return new HtmlBuilder()
                .append("根据")
                .append("[" + s + "]")
                .append("找不到对应的Service或Service Interface定义。")
                .toString();
    }
}
