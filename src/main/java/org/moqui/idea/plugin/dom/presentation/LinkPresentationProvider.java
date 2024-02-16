package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Link;
import org.moqui.idea.plugin.dom.model.Section;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class LinkPresentationProvider extends PresentationProvider<Link> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(Link coordinates) {
    String text =MyDomUtils.getXmlAttributeValueString(coordinates.getText().getXmlAttributeValue())
            .orElse(MyStringUtils.EMPTY_STRING);

    String url = MyDomUtils.getXmlAttributeValueString(coordinates.getUrl().getXmlAttributeValue())
            .orElse(UNKNOWN);
    String str;
    if(text.equals(MyStringUtils.EMPTY_STRING)) {
      str = url;
    }else {
      str  = text+" ( "+url+" )";
    }

    return MyStringUtils.formatPresentationName(Link.TAG_NAME,str);
  }
}
