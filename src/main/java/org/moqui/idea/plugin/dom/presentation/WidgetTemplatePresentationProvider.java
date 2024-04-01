package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Eeca;
import org.moqui.idea.plugin.dom.model.WidgetTemplate;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class WidgetTemplatePresentationProvider extends PresentationProvider<WidgetTemplate> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(WidgetTemplate coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(UNKNOWN);
    return MyStringUtils.formatPresentationName(WidgetTemplate.TAG_NAME,str);
  }
}
