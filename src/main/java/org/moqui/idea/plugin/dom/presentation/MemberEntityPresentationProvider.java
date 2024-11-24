package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.MemberEntity;
import org.moqui.idea.plugin.util.MyDomUtils;

public class MemberEntityPresentationProvider extends PresentationProvider<MemberEntity> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(MemberEntity coordinates) {
    String name;
    name = MyDomUtils.getXmlAttributeValueString(coordinates.getEntityName().getXmlAttributeValue())
            .orElse(UNKNOWN);

    if(coordinates.getEntityAlias().exists()){
      name = name + " (Alias: " + coordinates.getEntityAlias().getStringValue()+")";
    }
    return name;
  }
}
