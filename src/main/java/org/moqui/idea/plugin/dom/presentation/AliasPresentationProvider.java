package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Alias;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class AliasPresentationProvider extends PresentationProvider<Alias> {

  @Nullable
  @Override
  public String getName(Alias coordinates) {
    String name;
    name = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);
    if(coordinates.getEntityAlias().exists()){
      name = name + " (Member: " + coordinates.getEntityAlias().getStringValue()+")";
    }
    if(coordinates.getField().exists()){
      name = name + " (Field: " + coordinates.getField().getStringValue()+")";
    }

    return MyStringUtils.formatPresentationName(Alias.TAG_NAME,name);
  }
}
