package org.moqui.idea.plugin.dom.converter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.WidgetTemplateInclude;
import org.moqui.idea.plugin.util.LocationUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.WidgetTemplateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * 针对location的处理，location有多种形式：
 * 1、component://SimpleScreens/template/party/PartyForms.xml（指向一个文件 ）
 * 2、component://SimpleScreens/screen/SimpleScreens/Search.xml#SearchOptions（指向文件中的某个tag）
 * 3、location="moquiservice:moqui.example.ExampleServices.targetCamelExample"
 *     camel服务的路径，
 * 4、 location="${wikiSpace.decoratorScreenLocation}"，动态内容，无法处理不进行处理
 * 5、 location="classpath://service/org/moqui/impl/BasicServices.xml"，service-file、service-include中使用
 *     和component类似，对应到某个文件，只是没有指定component名称，但应该也是唯一的
 */

public class LocationConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter<String> {
    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext context) {
        Optional<WidgetTemplateInclude> widgetTemplateOptional = MyDomUtils.getLocalDomElementByConvertContext(context, WidgetTemplateInclude.class);
        if(widgetTemplateOptional.isPresent()) {
            return WidgetTemplateUtils.getWidgetTemplateIncludeLocations(context.getProject());
        }
        return new ArrayList<>();
    }

    @Override
    public  @NotNull PsiReference[] createReferences(GenericDomValue<String> value, PsiElement element, ConvertContext context) {
        return LocationUtils.createReferences(value,element,context);
    }

}
