package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Eeca;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class EecaPresentationProvider extends PresentationProvider<Eeca> {

  @Nullable
  @Override
  public String getName(Eeca coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getId().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);
    return MyStringUtils.formatPresentationName(Eeca.TAG_NAME,str);
  }
}
