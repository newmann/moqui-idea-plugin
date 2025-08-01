package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.*;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.RelationshipReferenceConverter;

import java.util.List;

public interface Detail extends DomElement {

    String TAG_NAME = "detail";


    String ATTR_RELATIONSHIP="relationship";

    String ATTR_USE_MASTER="use-master";

    @NotNull
    @Attribute(ATTR_RELATIONSHIP)
    @Referencing(RelationshipReferenceConverter.class)
    GenericAttributeValue<String> getRelationship();
    
    @NotNull
    @Attribute(ATTR_USE_MASTER)
    GenericAttributeValue<String> getUseMaster();

    @NotNull
    @SubTagList(Detail.TAG_NAME)
    List<Detail> getDetailList();

}
