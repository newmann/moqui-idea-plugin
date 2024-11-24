package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.AliasAll;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class AliasAllPresentationProvider extends PresentationProvider<AliasAll> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(AliasAll coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getEntityAlias().getXmlAttributeValue())
            .orElse(UNKNOWN);
    return MyStringUtils.formatPresentationName(AliasAll.TAG_NAME,str);
  }
}
