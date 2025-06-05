package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.DefaultResponse;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class DefaultResponsePresentationProvider extends PresentationProvider<DefaultResponse> {


  @Nullable
  @Override
  public String getName(DefaultResponse coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getUrl().getXmlAttributeValue())
                    .orElse(MyStringUtils.UNKNOWN);

    return MyStringUtils.formatPresentationName(DefaultResponse.TAG_NAME,str);
  }
}
