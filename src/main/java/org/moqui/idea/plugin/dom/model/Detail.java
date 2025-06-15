package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.Convert;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.SubTagList;
import com.intellij.util.xmlb.annotations.Attribute;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.converter.RelationshipConverter;

import java.util.List;

public interface Detail extends DomElement {

    public static final String TAG_NAME = "detail";


    public static final String ATTR_RELATIONSHIP="relationship";

    public static final String ATTR_USE_MASTER="use-master";

    @NotNull
    @Attribute(ATTR_RELATIONSHIP)
    @Convert(RelationshipConverter.class)
    GenericAttributeValue<String> getRelationship();
    
    @NotNull
    @Attribute(ATTR_USE_MASTER)
    GenericAttributeValue<String> getUseMaster();

    @NotNull
    @SubTagList(Detail.TAG_NAME)
    List<Detail> getDetailList();

}
