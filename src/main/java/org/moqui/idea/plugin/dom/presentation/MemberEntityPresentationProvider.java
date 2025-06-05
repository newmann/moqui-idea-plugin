package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.MemberEntity;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class MemberEntityPresentationProvider extends PresentationProvider<MemberEntity> {

  @Nullable
  @Override
  public String getName(MemberEntity coordinates) {
    String name;
    name = MyDomUtils.getXmlAttributeValueString(coordinates.getEntityName().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);

    if(coordinates.getEntityAlias().exists()){
      name = name + " (Alias: " + coordinates.getEntityAlias().getStringValue()+")";
    }
    return name;
  }
}
