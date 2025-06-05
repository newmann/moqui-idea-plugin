package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.EntityFindCount;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class EntityFindCountPresentationProvider extends PresentationProvider<EntityFindCount> {

  @Nullable
  @Override
  public String getName(EntityFindCount coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getEntityName().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);
    return MyStringUtils.formatPresentationName(EntityFindCount.TAG_NAME,str);
  }
}
