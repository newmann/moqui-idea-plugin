package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Eeca;
import org.moqui.idea.plugin.dom.model.Link;
import org.moqui.idea.plugin.dom.model.Section;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class SectionPresentationProvider extends PresentationProvider<Section> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(Section coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(UNKNOWN);
    return MyStringUtils.formatPresentationName(Section.TAG_NAME,str);
  }
}
