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
        if (service.getDescription().getValue() != null) {
            builder.br()
                   .append(text("Description: " + service.getDescription().getValue()));
        }
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
        HtmlBuilder fieldListBuilder = new HtmlBuilder();

        HtmlBuilder tableHeader = new HtmlBuilder();
        tableHeader.append(text(Field.ATTR_NAME).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(Field.ATTR_TYPE).wrapWith("th"));
        tableHeader.append(nbsp().wrapWith("th"));
        tableHeader.append(text(AliasAll.ATTR_PREFIX).wrapWith("th"));

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
                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getPrefix())).wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(text(MyDomUtils.getValueOrFalseBoolean(field.getNotNull())? "Y" : "").wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(nbsp().wrapWith(SECTION_CONTENT_CELL));
//                    fieldBuilder.append(text(MyDomUtils.getValueOrEmptyString(field.getDescription().getValue())).wrapWith(SECTION_CONTENT_CELL));

                    fieldListBuilder.append(fieldBuilder.wrapWith("tr"));
                }
        );

        return fieldListBuilder.wrapWith(SECTIONS_TABLE);
    }

    public static HtmlChunk.Element formatTagValue(XmlTag xmlTag,String title, @NotNull String noContentMessage) {
        HtmlBuilder builder = new HtmlBuilder();
        String content =xmlTag == null? null : xmlTag.getValue().getText();

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
