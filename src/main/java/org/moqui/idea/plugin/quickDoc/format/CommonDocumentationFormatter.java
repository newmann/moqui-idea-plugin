package org.moqui.idea.plugin.quickDoc.format;

import com.intellij.openapi.util.text.HtmlBuilder;
import com.intellij.openapi.util.text.HtmlChunk;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.*;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexService;
import org.moqui.idea.plugin.service.IndexServiceParameter;
import org.moqui.idea.plugin.util.ComponentUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Comparator;
import java.util.List;

import static com.intellij.lang.documentation.DocumentationMarkup.*;
import static com.intellij.openapi.util.text.HtmlChunk.nbsp;
import static com.intellij.openapi.util.text.HtmlChunk.text;

public class CommonDocumentationFormatter {
    public static String formatNavigateDocWithDomElement(DomElement element, String elementName) {
        HtmlBuilder docBuilder = new HtmlBuilder();
        String containingFile = MyDomUtils.getContainingFileNameFromPsiElement(element.getXmlTag()).orElse("N/A");
        docBuilder.append(text("Moqui " + elementName))
                .br()
                .append(text("Defined in " + containingFile + " [component: " + "]"));
        return docBuilder.toString();
    }

    public static HtmlChunk.Element formatEntityOrViewDefinition(DomElement element) {
        boolean isView = element instanceof ViewEntity;
        boolean isEntity = element instanceof Entity;
        if (!isView && !isEntity) return null;
        HtmlBuilder builder = new HtmlBuilder();
        AbstractEntity abstractEntity = (AbstractEntity) element;

        String fileName = MyDomUtils.getContainingFileNameFromPsiElement(element.getXmlTag()).orElse("N/A");
        String componentName = MyDomUtils.getContainingPathFromPsiElement(element.getXmlTag())
                .flatMap(ComponentUtils::getComponentNameFromPath)
                .orElse(MyStringUtils.EMPTY_STRING);

        builder.append(text((isView ? "view" : "entity") + " name="))
                .append(text("\"" + MyDomUtils.getValueOrEmptyString(abstractEntity.getEntityName()) + "\"").bold())
                .append(nbsp())
                .append(text("package-name=\"" + MyDomUtils.getValueOrEmptyString(abstractEntity.getPackage()) + "\""))
                .br()
                .append(text("Defined in " + fileName + ", [Component: " + componentName + " ]").wrapWith(GRAYED_ELEMENT));
        return builder.wrapWith("pre").wrapWith(DEFINITION_ELEMENT);
    }
    public static HtmlChunk.Element formatServiceDefinition(Service service) {
        HtmlBuilder builder = new HtmlBuilder();

        String fileName = MyDomUtils.getContainingFileNameFromPsiElement(service.getXmlTag()).orElse("N/A");
        String componentName = MyDomUtils.getContainingPathFromPsiElement(service.getXmlTag())
                .flatMap(ComponentUtils::getComponentNameFromPath)
                .orElse(MyStringUtils.EMPTY_STRING);

        builder.append(text(ServiceUtils.getFullNameFromService(service)).bold())
                .br()
                .append(text("Defined in " + fileName + ", [Component: " + componentName + " ]").wrapWith(GRAYED_ELEMENT));
        service.getImplementsList().forEach(anImplements -> builder.br()
                .append(text("Implements: " + MyDomUtils.getValueOrEmptyString(anImplements.getService())).wrapWith(GRAYED_ELEMENT)));

        if (service.getDescription().getValue() != null) {
            builder.br()
                   .append(text("Description: " + service.getDescription().getValue()));
        }
        return builder.wrapWith("pre").wrapWith(DEFINITION_ELEMENT);
    }
    public static HtmlChunk.Element formatSectionDefinition(Section section) {
        HtmlBuilder builder = new HtmlBuilder();

        String fileName = MyDomUtils.getContainingFileNameFromPsiElement(section.getXmlTag()).orElse("N/A");
        String componentName = MyDomUtils.getContainingPathFromPsiElement(section.getXmlTag())
                .flatMap(ComponentUtils::getComponentNameFromPath)
                .orElse(MyStringUtils.EMPTY_STRING);

        builder.append(text(MyDomUtils.getValueOrEmptyString(section.getName())).bold())
                .br()
                .append(text("Defined in " + fileName + ", [Component: " + componentName + " ]").wrapWith(GRAYED_ELEMENT));

        return builder.wrapWith("pre").wrapWith(DEFINITION_ELEMENT);
    }

    public static HtmlChunk.Element formatEntityDefinition(Entity entity) {
        return formatEntityOrViewDefinition(entity);
    }
    public static HtmlChunk.Element formatViewEntityDefinition(ViewEntity view) {
        return formatEntityOrViewDefinition(view);
    }

    public static HtmlChunk.Element formatExtendEntityList(List<ExtendEntity> extendEntityList) {
        HtmlBuilder builder = new HtmlBuilder();
        extendEntityList.forEach(extendEntity -> {
                    builder.hr();
                    builder.append(formatExtendEntity(extendEntity));
                }
        );
        return builder.wrapWith(CONTENT_ELEMENT);
    }
    public static HtmlChunk.Element formatExtendEntity(ExtendEntity extendEntity) {
        List<Field> extendedFieldList = extendEntity.getFieldList();
        String fileName = MyDomUtils.getContainingFileNameFromPsiElement(extendEntity.getXmlTag()).orElse("N/A");
        String componentName = MyDomUtils.getContainingPathFromPsiElement(extendEntity.getXmlTag())
                .flatMap(ComponentUtils::getComponentNameFromPath)
                .orElse(MyStringUtils.EMPTY_STRING);

        HtmlBuilder extendBuilder = new HtmlBuilder()
                .append(text("Extended ").bold())
                .nbsp()
                .append(text("in file " + fileName +" [component " + componentName + " ]"))
                .append(text(", with fields: "))
                .append(formatFieldList(extendedFieldList));

        return extendBuilder.wrapWith(CONTENT_ELEMENT);
    }
    public static HtmlChunk.Element formatFieldList(List<Field> fieldList) {

//        List<Field> sortedList = fieldList.stream()
//                .sorted(new Comparator<Field>() {
//                    @Override
//                    public int compare(Field field, Field t1) {
//                        String n1 = MyDomUtils.getValueOrEmptyString(field.getName());
//                        String n2= MyDomUtils.getValueOrEmptyString(t1.getName());
//                        return n1.compareTo(n2);
//                    }
//                })
//                .toList();
        HtmlBuilder fieldListBuilder = new HtmlBuilder();

        HtmlBuilder tableHeader = new HtmlBuilder();
        tableHeader.append(text(Field.ATTR_NAME).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(Field.ATTR_TYPE).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(Field.ATTR_IS_PK).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(Field.ATTR_NOT_NULL).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(Description.TAG_NAME).wrapWith("th"));

        fieldListBuilder.append(tableHeader.wrapWith("tr"));

        fieldList.forEach(field -> {

            HtmlBuilder fieldBuilder = new HtmlBuilder();
            fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getName())).wrapWith(SECTION_CONTENT_CELL));
            fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
            fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getType())).wrapWith(GRAYED_ELEMENT).wrapWith(SECTION_CONTENT_CELL));
            fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
            fieldBuilder.append(text(MyDomUtils.getValueOrFalseBoolean(field.getIsPk())? "Y" : "").wrapWith(SECTION_CONTENT_CELL));
            fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
            fieldBuilder.append(text(MyDomUtils.getValueOrFalseBoolean(field.getNotNull())? "Y" : "").wrapWith(SECTION_CONTENT_CELL));
            fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
            fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getDescription().getValue())).wrapWith(SECTION_CONTENT_CELL));

            fieldListBuilder.append(fieldBuilder.wrapWith("tr"));
        }
        );

        return fieldListBuilder.wrapWith(SECTIONS_TABLE);
    }
    public static HtmlChunk.Element formatAbstractFieldList(List<AbstractField> fieldList) {
        HtmlBuilder fieldListBuilder = new HtmlBuilder();

        HtmlBuilder tableHeader = new HtmlBuilder();
        tableHeader.append(text(Field.ATTR_NAME).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(Field.ATTR_TYPE).wrapWith("th"));
//        tableHeader.append(nbsp().wrapWith("th"));
//        tableHeader.append(text(Field.ATTR_IS_PK).wrapWith("th"));
//        tableHeader.append(nbsp().wrapWith("th"));
//        tableHeader.append(text(Field.ATTR_NOT_NULL).wrapWith("th"));
//        tableHeader.append(nbsp().wrapWith("th"));
//        tableHeader.append(text(Description.TAG_NAME).wrapWith("th"));

        fieldListBuilder.append(tableHeader.wrapWith("tr"));

        fieldList.forEach(field -> {

                    HtmlBuilder fieldBuilder = new HtmlBuilder();
                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getName())).wrapWith(SECTION_CONTENT_CELL));
                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getType())).wrapWith(GRAYED_ELEMENT).wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(text(MyDomUtils.getValueOrFalseBoolean(field.getIsPk())? "Y" : "").wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(text(MyDomUtils.getValueOrFalseBoolean(field.getNotNull())? "Y" : "").wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getDescription().getValue())).wrapWith(SECTION_CONTENT_CELL));

                    fieldListBuilder.append(fieldBuilder.wrapWith("tr"));
                }
        );

        return fieldListBuilder.wrapWith(SECTIONS_TABLE);
    }
    public static HtmlChunk.Element formatIndexAbstractFieldList(List<IndexAbstractField> fieldList) {

        List<IndexAbstractField> sortedList = fieldList.stream()
                .sorted(Comparator.comparing(IndexAbstractField::getName))
                .toList();

        HtmlBuilder fieldListBuilder = new HtmlBuilder();

        HtmlBuilder tableHeader = new HtmlBuilder();
        tableHeader.append(text(Field.ATTR_NAME).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(Field.ATTR_TYPE).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(AliasAll.ATTR_PREFIX).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(Alias.ATTR_FUNCTION).wrapWith("th"));

        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text("Entity Name").wrapWith("th"));
//        tableHeader.append(nbsp().wrapWith("th"));
//        tableHeader.append(text(Description.TAG_NAME).wrapWith("th"));

        fieldListBuilder.append(tableHeader.wrapWith("tr"));

        sortedList.forEach(field -> {

                    HtmlBuilder fieldBuilder = new HtmlBuilder();
                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getName())).wrapWith(SECTION_CONTENT_CELL));
                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getType())).wrapWith(GRAYED_ELEMENT).wrapWith(SECTION_CONTENT_CELL));
                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getPrefix())).wrapWith(SECTION_CONTENT_CELL));
                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
                    if(field.getAbstractField() instanceof Alias aliasField) {
                        fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(aliasField.getFunction())).wrapWith(SECTION_CONTENT_CELL));
                    }else {
                        fieldBuilder.append(text(MyStringUtils.EMPTY_STRING).wrapWith(SECTION_CONTENT_CELL));
                    }

                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getFromAbstractIndexEntity().getShortName())).wrapWith(SECTION_CONTENT_CELL));
//                    if(field.getAliasAll() == null) {
//                        if(field.getAbstractField() instanceof Alias aliasField) {
//                            fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(aliasField.getEntityAlias())).wrapWith(SECTION_CONTENT_CELL));
//                        }else {
//                            fieldBuilder.append(text(MyStringUtils.EMPTY_STRING).wrapWith(SECTION_CONTENT_CELL));
//                        }
//                    }else{
//                        fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getAliasAll().getEntityAlias())).wrapWith(SECTION_CONTENT_CELL));
//                    }
//                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getDescription().getValue())).wrapWith(SECTION_CONTENT_CELL));

                    fieldListBuilder.append(fieldBuilder.wrapWith("tr"));
                }
        );

        return fieldListBuilder.wrapWith(SECTIONS_TABLE);
    }
    public static HtmlChunk.Element formatIndexServiceInOutParameter(IndexService indexService) {
        HtmlBuilder serviceParameter = new HtmlBuilder();
        if(indexService == null) {
            serviceParameter.append(HtmlChunk.text("Can't find IndexService"));
        }else {
            serviceParameter.append(HtmlChunk.text("In Parameter:").italic().bold());
            if(indexService.getInParametersAbstractFieldList().isEmpty()) {
                serviceParameter.br().append(HtmlChunk.text("Not define in parameters"));
            }else {
                serviceParameter.append(formatIndexServiceParameterList(indexService.getInParametersAbstractFieldList()));
            }
            serviceParameter.append(HtmlChunk.text("Out Parameter:").italic().bold());
            if(indexService.getOutParametersAbstractFieldList().isEmpty()) {
                serviceParameter.br().append(HtmlChunk.text("Not define out parameters"));
            }else {
                serviceParameter.append(formatIndexServiceParameterList(indexService.getOutParametersAbstractFieldList()));
            }


//            docBuilder.append(formatTagValue(service.getInParameters().getXmlTag(), "In Parameters:", "Not define in parameters"));
//            docBuilder.append(formatTagValue(service.getOutParameters().getXmlTag(), "Out Parameters:", "Not define out parameters"));
        }
        return serviceParameter.wrapWith(CONTENT_ELEMENT);
    }

    public static HtmlChunk.Element formatIndexServiceParameterList(List<IndexServiceParameter> parameterList) {
        List<IndexServiceParameter> sortedList = parameterList.stream()
                .sorted(Comparator.comparing(IndexServiceParameter::getParameterName))
                .toList();

        HtmlBuilder parameterListBuilder = new HtmlBuilder();

        sortedList.forEach(parameter -> {
                    boolean isRequired = false;
                    if(parameter.getAbstractField() instanceof Parameter parameterInstance) {
                        isRequired = MyDomUtils.getValueOrFalseBoolean(parameterInstance.getRequired());
                    }

                    HtmlBuilder parameterBuilder = new HtmlBuilder();
                    parameterBuilder.append(text(parameter.getParameterName()));
                    parameterBuilder.append(text((parameter.getType().isEmpty() ? "" : " [ "+ parameter.getType()+" ]")).wrapWith(GRAYED_ELEMENT));
                    parameterBuilder.append(text(isRequired ? " - required" : "").bold());
//                    parameterBuilder.append(text(
//                            parameter.getParameterName()
//                            + (parameter.getType().isEmpty() ? "" : " [ "+ parameter.getType()+" ]")
//                            + (isRequired ? " - required" : "")
//                        ).wrapWith("span"));
                    if(!parameter.getChildParameterList().isEmpty()) {
                        parameterBuilder.append(formatIndexServiceParameterList(parameter.getChildParameterList()));
                    }

                    parameterListBuilder.append(parameterBuilder.wrapWith("li"));
                }
        );

        return parameterListBuilder.wrapWith("ul");
    }

    public static HtmlChunk.Element formatTagValue(XmlTag xmlTag,String title, @NotNull String noContentMessage) {
        HtmlBuilder builder = new HtmlBuilder();
        String content =xmlTag == null? null : xmlTag.getText();

        if(content == null) {
            content = noContentMessage;
        }
        if(title != null && !title.isEmpty()) {
            builder.append(HtmlChunk.text(title).bold());
        }
        builder.append(HtmlChunk.text(MyStringUtils.cleanStringForQuickDocumentation(content)).wrapWith("pre"));
        return  builder.wrapWith(CONTENT_ELEMENT);
    }

}
