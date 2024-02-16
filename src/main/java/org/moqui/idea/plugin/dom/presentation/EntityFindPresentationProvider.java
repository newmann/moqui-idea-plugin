package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.EntityFind;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class EntityFindPresentationProvider extends PresentationProvider<EntityFind> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(EntityFind coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getEntityName().getXmlAttributeValue())
            .orElse(UNKNOWN);
    return MyStringUtils.formatPresentationName(EntityFind.TAG_NAME,str);
  }
}
