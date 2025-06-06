package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.EntityFindOne;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class EntityFindOnePresentationProvider extends PresentationProvider<EntityFindOne> {

  @Nullable
  @Override
  public String getName(EntityFindOne coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getEntityName().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);
    return MyStringUtils.formatPresentationName(EntityFindOne.TAG_NAME,str);
  }
}
