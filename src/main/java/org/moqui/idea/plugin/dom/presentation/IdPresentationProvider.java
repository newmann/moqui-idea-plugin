package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Id;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class IdPresentationProvider extends PresentationProvider<Id> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(Id coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(UNKNOWN);

    return MyStringUtils.formatPresentationName(Id.TAG_NAME,str);
  }
}
