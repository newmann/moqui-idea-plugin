package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.SectionIterate;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class SectionIteratePresentationProvider extends PresentationProvider<SectionIterate> {

  @Nullable
  @Override
  public String getName(SectionIterate coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);
    return MyStringUtils.formatPresentationName(SectionIterate.TAG_NAME,str);
  }
}
