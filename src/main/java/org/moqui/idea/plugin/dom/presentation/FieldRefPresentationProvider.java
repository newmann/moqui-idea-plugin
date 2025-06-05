package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.FieldRef;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class FieldRefPresentationProvider extends PresentationProvider<FieldRef> {
  @Nullable
  @Override
  public String getName(FieldRef coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);
    return MyStringUtils.formatPresentationName(FieldRef.TAG_NAME,str);
  }
}
