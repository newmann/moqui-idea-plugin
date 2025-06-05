package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Method;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class MethodPresentationProvider extends PresentationProvider<Method> {

  @Nullable
  @Override
  public String getName(Method coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getType().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);
    return MyStringUtils.formatPresentationName(Method.TAG_NAME,str);
  }
}
