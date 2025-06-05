package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Relationship;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class RelationshipPresentationProvider extends PresentationProvider<Relationship> {

  @Nullable
  @Override
  public String getName(Relationship coordinates) {
    String name;
    name = MyDomUtils.getXmlAttributeValueString(coordinates.getRelated().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);
    if(coordinates.getTitle().exists()){
      name = name + " (T: " + coordinates.getTitle().getStringValue()+")";
    }
    if(coordinates.getShortAlias().exists()){
      name = name + " (S: " + coordinates.getShortAlias().getStringValue()+")";
    }

    return name;
  }
}
