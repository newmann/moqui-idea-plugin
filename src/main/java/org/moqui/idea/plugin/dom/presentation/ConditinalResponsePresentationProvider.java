package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.ConditionalResponse;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class ConditinalResponsePresentationProvider extends PresentationProvider<ConditionalResponse> {

  @Nullable
  @Override
  public String getName(ConditionalResponse coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getUrl().getXmlAttributeValue())
                    .orElse(MyStringUtils.UNKNOWN);

    return MyStringUtils.formatPresentationName(ConditionalResponse.TAG_NAME,str);
  }
}
