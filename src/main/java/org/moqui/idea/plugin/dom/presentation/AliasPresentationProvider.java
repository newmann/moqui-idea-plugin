package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Alias;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class AliasPresentationProvider extends PresentationProvider<Alias> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(Alias coordinates) {
    String name;
    name = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(UNKNOWN);
    if(coordinates.getEntityAlias().exists()){
      name = name + " (Member: " + coordinates.getEntityAlias().getStringValue()+")";
    }
    if(coordinates.getField().exists()){
      name = name + " (Field: " + coordinates.getField().getStringValue()+")";
    }

    return MyStringUtils.formatPresentationName(Alias.TAG_NAME,name);
  }
}
