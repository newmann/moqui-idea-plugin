package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Relationship;
import org.moqui.idea.plugin.util.MyDomUtils;

public class RelationshipPresentationProvider extends PresentationProvider<Relationship> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(Relationship coordinates) {
    String name;
    name = MyDomUtils.getXmlAttributeValueString(coordinates.getRelated().getXmlAttributeValue())
            .orElse(UNKNOWN);
    if(coordinates.getTitle().exists()){
      name = name + " (T: " + coordinates.getTitle().getStringValue()+")";
    }
    if(coordinates.getShortAlias().exists()){
      name = name + " (S: " + coordinates.getShortAlias().getStringValue()+")";
    }

    return name;
  }
}
