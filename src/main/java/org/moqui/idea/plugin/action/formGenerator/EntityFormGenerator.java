package org.moqui.idea.plugin.action.formGenerator;

import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.FormSingle;
import org.moqui.idea.plugin.service.AbstractIndex;
import org.moqui.idea.plugin.service.AbstractIndexEntity;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.util.MNode;

import java.util.Map;
import java.util.Optional;

public class EntityFormGenerator implements FormGenerator{
    @Override
    public <T extends AbstractIndex> Optional<String> generatorFormSingle(@NotNull T indexItem, @NotNull FormSingleGenerateType generateType) {
        if(indexItem instanceof AbstractIndexEntity abstractIndexEntity) {
            StringBuilder xmlBuilder = new StringBuilder();
            String formName = generateType.name() + abstractIndexEntity.getShortName();
            String transitionName = MyStringUtils.lowerCaseFirstChar(formName);

            MNode formNode = new MNode(FormSingle.TAG_NAME, Map.of(FormSingle.ATTR_NAME,formName,FormSingle.ATTR_TRANSITION,transitionName));

            abstractIndexEntity.getIndexAbstractFieldList().stream().forEach(indexAbstractField ->{

            } );
            return Optional.empty();
        }else {
            return Optional.empty();
        }
    }

    @Override
    public <T extends AbstractIndex> Optional<String> generatorFormList(@NotNull T indexItem) {
        return Optional.empty();
    }
    void addAutoEntityField(AbstractIndexEntity abstractIndexEntity, String fieldName, String fieldType, MNode newFieldNode, MNode subFieldNode, MNode baseFormNode) {
        // NOTE: in some cases this may be null
        IndexAbstractField abstractField = abstractIndexEntity.getIndexAbstractField(fieldName).orElse(null);
        if(abstractField == null) return;

        String efType = abstractField.getType() ;
        if(MyStringUtils.isEmpty(efType)) efType = "text-long";

        // to see if this should be a drop-down with data from another entity,
        // find first relationship that has this field as the only key map and is not a many relationship
        MNode oneRelNode = null;
        Map oneRelKeyMap = null;
        String relatedEntityName = null;
//        EntityDefinition relatedEd = null
//        for (RelationshipInfo relInfo in ed.getRelationshipsInfo(false)) {
//            String relEntityName = relInfo.relatedEntityName
//            EntityDefinition relEd = relInfo.relatedEd
//            Map km = relInfo.keyMap
//            if (km.size() == 1 && km.containsKey(fieldName) && relInfo.type == "one" && relInfo.relNode.attribute("is-auto-reverse") != "true") {
//                oneRelNode = relInfo.relNode
//                oneRelKeyMap = km
//                relatedEntityName = relEntityName
//                relatedEd = relEd
//                break
//            }
//        }
//        String keyField = (String) oneRelKeyMap?.keySet()?.iterator()?.next()
//        String relKeyField = (String) oneRelKeyMap?.values()?.iterator()?.next()
//        String relDefaultDescriptionField = relatedEd?.getDefaultDescriptionField()
//
//        switch (fieldType) {
//            case "edit":
//                // lastUpdatedStamp is always hidden for edit (needed for optimistic lock)
//                if (fieldName == "lastUpdatedStamp") {
//                    subFieldNode.append("hidden", null)
//                    break
//                }
//
//                // handle header-field
//                if (baseFormNode.name == "form-list" && !newFieldNode.hasChild("header-field"))
//                    newFieldNode.append("header-field", ["show-order-by":"true"])
//
//                // handle sub field (default-field)
//                if (subFieldNode == null) break
//            /* NOTE: used to do this but doesn't make sense for main use of this in ServiceRun/etc screens; for app
//                forms should separates pks and use display or hidden instead of edit:
//            List<String> pkFieldNameSet = ed.getPkFieldNames()
//            if (pkFieldNameSet.contains(fieldName) && serviceVerb == "update") {
//                subFieldNode.append("hidden", null)
//            } else {
//            }
//            */
//                if (efType.startsWith("date") || efType.startsWith("time")) {
//                    MNode dateTimeNode = subFieldNode.append("date-time", [type:efType])
//                    if (fieldName == "fromDate") dateTimeNode.attributes.put("default-value", "\${ec.l10n.format(ec.user.nowTimestamp, 'yyyy-MM-dd HH:mm')}")
//                } else if ("text-long".equals(efType) || "text-very-long".equals(efType)) {
//                    subFieldNode.append("text-area", null)
//                } else if ("text-indicator".equals(efType)) {
//                    MNode dropDownNode = subFieldNode.append("drop-down", ["allow-empty":"true"])
//                    dropDownNode.append("option", ["key":"Y"])
//                    dropDownNode.append("option", ["key":"N"])
//                } else if ("binary-very-long".equals(efType)) {
//                    // would be nice to have something better for this, like a download somehow
//                    subFieldNode.append("display", null)
//                } else {
//                    if (oneRelNode != null) {
//                        addEntityFieldDropDown(oneRelNode, subFieldNode, relatedEd, relKeyField, "")
//                    } else {
//                        if (efType.startsWith("number-") || efType.startsWith("currency-")) {
//                            subFieldNode.append("text-line", [size:"10"])
//                        } else {
//                            subFieldNode.append("text-line", [size:"30"])
//                        }
//                    }
//                }
//                break
//            case "find":
//                // handle header-field
//                if (baseFormNode.name == "form-list" && !newFieldNode.hasChild("header-field"))
//                    newFieldNode.append("header-field", ["show-order-by":"case-insensitive"])
//                // handle sub field (default-field)
//                if (subFieldNode == null) break
//                if (efType.startsWith("date") || efType.startsWith("time")) {
//                    subFieldNode.append("date-find", [type:efType])
//                } else if (efType.startsWith("number-") || efType.startsWith("currency-")) {
//                    subFieldNode.append("range-find", null)
//                } else {
//                    if (oneRelNode != null) {
//                        addEntityFieldDropDown(oneRelNode, subFieldNode, relatedEd, relKeyField, "")
//                    } else {
//                        subFieldNode.append("text-find", null)
//                    }
//                }
//                break
//            case "display":
//                // handle header-field
//                if (baseFormNode.name == "form-list" && !newFieldNode.hasChild("header-field"))
//                    newFieldNode.append("header-field", ["show-order-by":"case-insensitive"])
//                // handle sub field (default-field)
//                if (subFieldNode == null) break
//                String textStr
//                if (relDefaultDescriptionField) textStr = "\${" + relDefaultDescriptionField + " ?: ''} [\${" + relKeyField + "}]"
//                else textStr = "[\${" + relKeyField + "}]"
//                if (oneRelNode != null) {
//                    subFieldNode.append("display-entity",
//                            ["entity-name":(oneRelNode.attribute("related") ?: oneRelNode.attribute("related-entity-name")), "text":textStr])
//                } else {
//                    Map<String, String> attrs = (Map<String, String>) null
//                    if (efType.equals("currency-amount")) {
//                        attrs = [format:"#,##0.00"]
//                    } else if (efType.equals("currency-precise")) {
//                        attrs = [format:"#,##0.000"]
//                    }
//                    subFieldNode.append("display", attrs)
//                }
//                break
//            case "find-display":
//                // handle header-field
//                if (baseFormNode.name == "form-list" && !newFieldNode.hasChild("header-field"))
//                    newFieldNode.append("header-field", ["show-order-by":"case-insensitive"])
//                MNode headerFieldNode = newFieldNode.hasChild("header-field") ?
//                        newFieldNode.first("header-field") : newFieldNode.append("header-field", null)
//                if ("date".equals(efType) || "time".equals(efType)) {
//                    headerFieldNode.append("date-find", [type:efType])
//                } else if ("date-time".equals(efType)) {
//                    headerFieldNode.append("date-period", [time:"true"])
//                } else if (efType.startsWith("number-") || efType.startsWith("currency-")) {
//                    headerFieldNode.append("range-find", [size:'10'])
//                    newFieldNode.attributes.put("align", "right")
//                    String function = fieldInfo?.fieldNode?.attribute("function")
//                    if (function != null && function in ['min', 'max', 'avg']) {
//                        newFieldNode.attributes.put("show-total", function)
//                    } else {
//                        newFieldNode.attributes.put("show-total", "sum")
//                    }
//                } else {
//                    if (oneRelNode != null) {
//                        addEntityFieldDropDown(oneRelNode, headerFieldNode, relatedEd, relKeyField, "")
//                    } else {
//                        headerFieldNode.append("text-find", [size:'30', "default-operator":"begins", "ignore-case":"false"])
//                    }
//                }
//                // handle sub field (default-field)
//                if (subFieldNode == null) break
//                if (oneRelNode != null) {
//                    String textStr
//                    if (relDefaultDescriptionField) textStr = "\${" + relDefaultDescriptionField + " ?: ''} [\${" + relKeyField + "}]"
//                    else textStr = "[\${" + relKeyField + "}]"
//                    subFieldNode.append("display-entity", ["text":textStr,
//                            "entity-name":(oneRelNode.attribute("related") ?: oneRelNode.attribute("related-entity-name"))])
//                } else {
//                    Map<String, String> attrs = (Map<String, String>) null
//                    if (efType.equals("currency-amount")) {
//                        attrs = [format:"#,##0.00"]
//                    } else if (efType.equals("currency-precise")) {
//                        attrs = [format:"#,##0.000"]
//                    }
//                    subFieldNode.append("display", attrs)
//                }
//                break
//            case "hidden":
//                subFieldNode.append("hidden", null)
//                break
//        }
//
//        // NOTE: don't like where this is located, would be nice to have a generic way for forms to add this sort of thing
//        if (oneRelNode != null && subFieldNode != null) {
//            if (internalFormNode.attribute("name") == "UpdateMasterEntityValue") {
//                MNode linkNode = subFieldNode.append("link", [url:"edit",
//                        text:("Edit ${relatedEd.getPrettyName(null, null)} [\${fieldValues." + keyField + "}]").toString(),
//                        condition:keyField, 'link-type':'anchor'] as Map<String, String>)
//                linkNode.append("parameter", [name:"aen", value:relatedEntityName])
//                linkNode.append("parameter", [name:relKeyField, from:"fieldValues.${keyField}".toString()])
//            }
//        }
    }
}
