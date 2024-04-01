package org.moqui.idea.plugin.dom.converter;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.ResolvingConverter;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.reference.PsiRef;
import org.moqui.idea.plugin.util.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

public class LocationConverter extends ResolvingConverter.StringConverter implements CustomReferenceConverter {
    @Override
    public @NotNull Collection<? extends String> getVariants(ConvertContext context) {
        return new ArrayList<>();
    }

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue value, PsiElement element, ConvertContext context) {
        String valueStr = value.getStringValue();
        if(valueStr ==null) return PsiReference.EMPTY_ARRAY;
        //todo：如果location中包含${,则表示是动态内容，无法处理
        if(valueStr.indexOf("${")>=0) return PsiReference.EMPTY_ARRAY;

        //如果location中包含#，表示该路径为WidgetTemplates，需要进一步处理
        String templateName = MyStringUtils.EMPTY_STRING;
        int poundIndex = valueStr.indexOf("#");
        if(poundIndex>=0) {
             templateName = valueStr.substring(poundIndex+1);
            valueStr = valueStr.substring(0,poundIndex);
        }else {
            if(!valueStr.contains("//")) {
                //可能是FormSingle或FormList的extends本文件中的名称
                return processLocalFormExtend(value,element,context);
            }
        }

        final String locationStr = valueStr;
        if(MyStringUtils.isEmpty(locationStr)) return PsiReference.EMPTY_ARRAY;
        //查找对应的文件
        PsiFile psiFile = MyDomUtils.getFileFromLocation(context.getProject(),locationStr).orElse(null);
        if(psiFile == null) return PsiReference.EMPTY_ARRAY;
        //将路径分解，每一级目录都分别对应
        String[] pathFileArray = MyDomUtils.getPathFileFromLocation(locationStr);

        final int pathFileArrayLength = pathFileArray.length;

        List<PsiReference> result = new ArrayList<>();

        //Service(location)，
        //TransitionInclude(location)
        //Transition，defaultResponse（url）


        int startIndex = locationStr.lastIndexOf("/");
        if(startIndex<0) {
            //沒有其他的目录
            result.add(new PsiRef(element,
                    new TextRange(1, locationStr.length()+1),
                    psiFile));
        }else {

            result.add(new PsiRef(element,
                    new TextRange(startIndex + 2, 1 + locationStr.length()),
                    psiFile));

            PsiDirectory psiDirectory = psiFile.getParent();

            int endOffsetIndex = startIndex + 1;
            int i = pathFileArrayLength - 2;
            while (i >= 0) {
                if (MyStringUtils.isEmpty(pathFileArray[i])) {
                    startIndex = startIndex - 1;

                    if(psiDirectory == null) break;

                    psiDirectory = psiDirectory.getParent();
                    continue;
                }

                result.add( new PsiRef(element,
                        new TextRange(endOffsetIndex - pathFileArray[i].length(), endOffsetIndex),
                        psiDirectory));
                endOffsetIndex = endOffsetIndex - pathFileArray[i].length() - 1;

                if(psiDirectory == null) break;
                psiDirectory = psiDirectory.getParent();
                i = i-1;
            }
        }



        if(MyStringUtils.isNotEmpty(templateName)) {
            String attributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
            String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
            int startOffset = locationStr.length() + 2;
            int endOffset = startOffset + templateName.length();

            //WidgetTemplateInclude（location）
            if(attributeName.equals(WidgetTemplateInclude.ATTR_LOCATION) && firstTagName.equals(WidgetTemplateInclude.TAG_NAME)) {
                WidgetTemplate widgetTemplate = WidgetTemplateUtils.getWidgetTemplateFromFileByName(psiFile, templateName)
                        .orElse(null);
                if (widgetTemplate == null) return result.toArray(new PsiReference[0]);

                result.add(new PsiRef(element,
                        new TextRange(startOffset, endOffset),
                        widgetTemplate.getName().getXmlAttributeValue()));
            }
            //FormSingle（extends）
            if(attributeName.equals(FormSingle.ATTR_EXTENDS)  && firstTagName.equals(FormSingle.TAG_NAME)) {
                FormSingle formSingle = ScreenUtils.getFormSingleFromScreenFileByName(psiFile, templateName)
                        .orElse(null);
                if (formSingle == null) return result.toArray(new PsiReference[0]);

                result.add(new PsiRef(element,
                        new TextRange(startOffset, endOffset),
                        formSingle.getName().getXmlAttributeValue()));
            }
            //FormList（extends）
            if(attributeName.equals(FormList.ATTR_EXTENDS) && firstTagName.equals(FormList.TAG_NAME)) {
                FormList formList = ScreenUtils.getFormListFromScreenFileByName(psiFile, templateName)
                        .orElse(null);
                if (formList == null) return result.toArray(new PsiReference[0]);

                result.add(new PsiRef(element,
                        new TextRange(startOffset, endOffset),
                        formList.getName().getXmlAttributeValue()));
            }

        }
        return result.toArray(new PsiReference[0]);
    }
    private PsiReference @NotNull [] processLocalFormExtend(GenericDomValue value, PsiElement element, ConvertContext context) {
        String valueStr = value.getStringValue();
        if(valueStr ==null) return PsiReference.EMPTY_ARRAY;

        String attributeName = MyDomUtils.getCurrentAttributeName(context).orElse(MyStringUtils.EMPTY_STRING);
        String firstTagName = MyDomUtils.getFirstParentTagName(context).orElse(MyStringUtils.EMPTY_STRING);
        PsiFile psiFile = context.getFile().getContainingFile();
        List<PsiReference> result = new ArrayList<>();

        if(attributeName.equals(FormSingle.ATTR_EXTENDS)  &&
                firstTagName.equals(FormSingle.TAG_NAME)
        ) {

            FormSingle formSingle = ScreenUtils.getFormSingleFromScreenFileByName(psiFile, valueStr)
                    .orElse(null);
            if (formSingle == null) return result.toArray(new PsiReference[0]);

            result.add(new PsiRef(element,
                    new TextRange(1, valueStr.length()+1),
                    formSingle.getName().getXmlAttributeValue()));
        }

        if(attributeName.equals(FormList.ATTR_EXTENDS)  &&
                firstTagName.equals(FormList.TAG_NAME)
        ) {

            FormList formList = ScreenUtils.getFormListFromScreenFileByName(psiFile, valueStr)
                    .orElse(null);
            if (formList == null) return result.toArray(new PsiReference[0]);

            result.add(new PsiRef(element,
                    new TextRange(1, valueStr.length()+1),
                    formList.getName().getXmlAttributeValue()));
        }

        return result.toArray(new PsiReference[0]);
    }
}
