package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Seca;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class SecaPresentationProvider extends PresentationProvider<Seca> {

  @Nullable
  @Override
  public String getName(Seca coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getId().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);
    return MyStringUtils.formatPresentationName(Seca.TAG_NAME,str);
  }
}
