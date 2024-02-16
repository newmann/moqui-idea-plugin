package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.MemberRelationship;
import org.moqui.idea.plugin.util.MyDomUtils;

public class MemberRelationPresentationProvider extends PresentationProvider<MemberRelationship> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(MemberRelationship coordinates) {
    String name;
    name = "MR: " + MyDomUtils.getXmlAttributeValueString(coordinates.getJoinFromAlias().getXmlAttributeValue())
            .orElse(UNKNOWN)
            +" - " + MyDomUtils.getXmlAttributeValueString(coordinates.getRelationship().getXmlAttributeValue())
            .orElse(UNKNOWN)
    ;
    if(coordinates.getEntityAlias().exists()){
      name = name + " (Alias: " + coordinates.getEntityAlias().getStringValue()+")";
    }
    return name;
  }
}
