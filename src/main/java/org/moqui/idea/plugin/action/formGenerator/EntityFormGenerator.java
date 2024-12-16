package org.moqui.idea.plugin.action.formGenerator;

import com.intellij.openapi.project.Project;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.FormSingle;
import org.moqui.idea.plugin.dom.model.KeyMap;
import org.moqui.idea.plugin.dom.model.Relationship;
import org.moqui.idea.plugin.service.AbstractIndex;
import org.moqui.idea.plugin.service.AbstractIndexEntity;
import org.moqui.idea.plugin.service.IndexAbstractField;
import org.moqui.idea.plugin.service.IndexEntity;
import org.moqui.idea.plugin.util.EntityUtils;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.util.MNode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EntityFormGenerator implements FormGenerator{
    @Override
    public <T extends AbstractIndex> Optional<String> generatorFormSingle(@NotNull Project project, @NotNull T indexItem, @NotNull FormSingleGenerateType generateType) {
        if(indexItem instanceof AbstractIndexEntity abstractIndexEntity) {
            StringBuilder xmlBuilder = new StringBuilder();
            String formName = generateType.name() + abstractIndexEntity.getShortName();
            String transitionName = MyStringUtils.lowerCaseFirstChar(formName);

            MNode formNode = new MNode(FormSingle.TAG_NAME, Map.of(FormSingle.ATTR_NAME,formName,FormSingle.ATTR_TRANSITION,transitionName));

            abstractIndexEntity.getIndexAbstractFieldList().forEach(indexAbstractField ->{
                String fieldName = indexAbstractField.getName();
                MNode newFieldNode = new MNode("field", Map.of("name",fieldName));
                MNode subFieldNode = newFieldNode.append("default-field", Map.of("validate-entity",abstractIndexEntity.getFullName(), "validate-field",fieldName));
                addAutoEntityField(project,indexAbstractField,fieldName, newFieldNode, subFieldNode, formNode);
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
    void addAutoEntityField(Project project, IndexAbstractField abstractField ,String fieldName, MNode newFieldNode, MNode subFieldNode, MNode baseFormNode) {
        // NOTE: in some cases this may be null

        if(abstractField == null) return;

        String efType = abstractField.getType() ;
        if(MyStringUtils.isEmpty(efType)) efType = "text-long";

        // to see if this should be a drop-down with data from another entity,
        // find first relationship that has this field as the only key map and is not a many relationship

        Relationship relationship = null;
        if(abstractField.getInAbstractIndexEntity() instanceof IndexEntity indexEntity) {
            for (Relationship relationshipItem : indexEntity.getRelationshipList()) {
                String relEntityName = MyDomUtils.getValueOrEmptyString(relationshipItem.getRelated());
                IndexEntity relEd = EntityUtils.getIndexEntityByName(project,relEntityName).orElse(null);
                List<KeyMap> km = relationshipItem.getKeyMapList();
                if (km.size() == 1 && MyDomUtils.getValueOrEmptyString(relationshipItem.getType()).equals("one")){
                    if(MyDomUtils.getValueOrEmptyString(km.get(0).getFieldName()).equals(fieldName) ) {
                        relationship = relationshipItem;
                        break;
                    }
                }
            }
        }
//
            // lastUpdatedStamp is always hidden for edit (needed for optimistic lock)
            if (fieldName.equals("lastUpdatedStamp")) {
                subFieldNode.append("hidden", null);

            }

            // handle header-field
            if (baseFormNode.getName().equals("form-list") && !newFieldNode.hasChild("header-field"))
                newFieldNode.append("header-field", Map.of("show-order-by","true"));

            // handle sub field (default-field)
            if (subFieldNode == null) return;
            /* NOTE: used to do this but doesn't make sense for main use of this in ServiceRun/etc screens; for app
                forms should separates pks and use display or hidden instead of edit:
            List<String> pkFieldNameSet = ed.getPkFieldNames()
            if (pkFieldNameSet.contains(fieldName) && serviceVerb == "update") {
                subFieldNode.append("hidden", null)
            } else {
            }
            */
            if (efType.startsWith("date") || efType.startsWith("time")) {
                MNode dateTimeNode = subFieldNode.append("date-time", Map.of("type",efType));
                if (fieldName.equals("fromDate")) dateTimeNode.getAttributes().put("default-value", "${ec.l10n.format(ec.user.nowTimestamp, 'yyyy-MM-dd HH:mm')}");
            } else if ("text-long".equals(efType) || "text-very-long".equals(efType)) {
                subFieldNode.append("text-area", null);
            } else if ("text-indicator".equals(efType)) {
                MNode dropDownNode = subFieldNode.append("drop-down",Map.of("allow-empty","true"));
                dropDownNode.append("option", Map.of("key","Y"));
                dropDownNode.append("option", Map.of("key","N"));
            } else if ("binary-very-long".equals(efType)) {
                // would be nice to have something better for this, like a download somehow
                subFieldNode.append("display", null);
            } else {
                if (relationship != null) {
                    addEntityFieldDropDown(subFieldNode, relationship,  "");
                } else {
                    if (efType.startsWith("number-") || efType.startsWith("currency-")) {
                        subFieldNode.append("text-line", Map.of("size","10"));
                    } else {
                        subFieldNode.append("text-line", Map.of("size","30"));
                    }
                }
            }
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
    protected void addEntityFieldDropDown(MNode subFieldNode, Relationship relationship,
                                           String dropDownStyle) {
        String title = MyDomUtils.getValueOrEmptyString(relationship.getTitle());

        if (relationship == null) {
            subFieldNode.append("text-line", null);
            return;
        }
        String relatedEntityName = MyDomUtils.getValueOrEmptyString(relationship.getRelated()); //relationship.getFullEntityName()
//        String relDefaultDescriptionField = relationship.getDefaultDescriptionField()

        // NOTE: combo-box not currently supported, so only show drop-down if less than 200 records
//        long recordCount
//        if (relatedEntityName == "moqui.basic.Enumeration") {
//            recordCount = ecfi.entityFacade.find("moqui.basic.Enumeration").condition("enumTypeId", title).disableAuthz().count()
//        } else if (relatedEntityName == "moqui.basic.StatusItem") {
//            recordCount = ecfi.entityFacade.find("moqui.basic.StatusItem").condition("statusTypeId", title).disableAuthz().count()
//        } else {
//            recordCount = ecfi.entityFacade.find(relatedEntityName).disableAuthz().count()
//        }
//        if (recordCount > 0 && recordCount <= 200) {
            // FOR FUTURE: use the combo-box just in case the drop-down as a default is over-constrained
            MNode dropDownNode = subFieldNode.append("drop-down", Map.of("allow-empty","true", "style",
                    (MyStringUtils.isEmpty(dropDownStyle) ? MyStringUtils.EMPTY_STRING: dropDownStyle)));
            MNode entityOptionsNode = dropDownNode.append("entity-options", null);
            MNode entityFindNode = entityOptionsNode.append("entity-find",
                    Map.of("entity-name",relatedEntityName, "offset","0", "limit","200"));

            if (relatedEntityName.equals("moqui.basic.Enumeration")) {
                // recordCount will be > 0 so we know there are records with this type
                entityFindNode.append("econdition", Map.of("field-name","enumTypeId", "value",title));
            } else if (relatedEntityName.equals("moqui.basic.StatusItem")) {
                // recordCount will be > 0 so we know there are records with this type
                entityFindNode.append("econdition", Map.of("field-name","statusTypeId", "value",title));
            }
//
//            if (relDefaultDescriptionField) {
//                entityOptionsNode.attributes.put("text", "\${" + relDefaultDescriptionField + " ?: ''} [\${" + relKeyField + "}]")
//                entityFindNode.append("order-by", ["field-name":relDefaultDescriptionField])
//            }
//        } else {
//            subFieldNode.append("text-line", null)
//        }
    }
}
