package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.DefaultResponse;
import org.moqui.idea.plugin.dom.model.Link;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class DefaultResponsePresentationProvider extends PresentationProvider<DefaultResponse> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(DefaultResponse coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getUrl().getXmlAttributeValue())
                    .orElse(UNKNOWN);

    return MyStringUtils.formatPresentationName(DefaultResponse.TAG_NAME,str);
  }
}
