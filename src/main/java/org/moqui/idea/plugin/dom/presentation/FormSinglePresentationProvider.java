package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.FormSingle;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class FormSinglePresentationProvider extends PresentationProvider<FormSingle> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(FormSingle coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(UNKNOWN);
    return MyStringUtils.formatPresentationName(FormSingle.TAG_NAME,str);
  }
}
