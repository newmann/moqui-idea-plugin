package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.DefaultField;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class DefaultFieldPresentationProvider extends PresentationProvider<DefaultField> {

  @Nullable
  @Override
  public String getName(DefaultField coordinates) {

    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getTitle().getXmlAttributeValue())
            .orElse(null);
    if(str == null) {
      return null;
    }else{
      return MyStringUtils.formatPresentationName(DefaultField.TAG_NAME, str);
    }
  }
}
