package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.FormList;
import org.moqui.idea.plugin.dom.model.FormSingle;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class FormListPresentationProvider extends PresentationProvider<FormList> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(FormList coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(UNKNOWN);
    return MyStringUtils.formatPresentationName(FormList.TAG_NAME,str);
  }
}
