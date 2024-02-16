package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Link;
import org.moqui.idea.plugin.dom.model.Resource;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class ResourcePresentationProvider extends PresentationProvider<Resource> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(Resource coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(UNKNOWN);
    return MyStringUtils.formatPresentationName(Resource.TAG_NAME,str);
  }
}
