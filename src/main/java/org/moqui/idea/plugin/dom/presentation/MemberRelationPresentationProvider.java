package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.dom.model.MemberRelationship;

public class MemberRelationPresentationProvider extends PresentationProvider<MemberRelationship> {

  @Nullable
  @Override
  public String getName(MemberRelationship coordinates) {
    String name;
    name = "MR: " + MyDomUtils.getXmlAttributeValueString(coordinates.getJoinFromAlias().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN)
            +" - " + MyDomUtils.getXmlAttributeValueString(coordinates.getRelationship().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN)
    ;
    if(coordinates.getEntityAlias().exists()){
      name = name + " (Alias: " + coordinates.getEntityAlias().getStringValue()+")";
    }
    return name;
  }
}
