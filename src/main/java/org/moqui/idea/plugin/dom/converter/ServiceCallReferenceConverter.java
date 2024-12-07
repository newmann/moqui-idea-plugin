package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.util.ServiceUtils;

/**
 * 服务全名有两种：
 * 1、服务调用：包名.动作#名称
 * 2、针对Entity的CRUD，动作#EntityFullName
 */
public class ServiceCallReferenceConverter implements CustomReferenceConverter<String> {


    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        return ServiceUtils.createServiceCallReferences(element);

    }

}
