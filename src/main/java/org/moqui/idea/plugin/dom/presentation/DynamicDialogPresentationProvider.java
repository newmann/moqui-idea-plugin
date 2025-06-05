package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.DynamicDialog;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class DynamicDialogPresentationProvider extends PresentationProvider<DynamicDialog> {

  @Nullable
  @Override
  public String getName(DynamicDialog coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getId().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);

    return MyStringUtils.formatPresentationName(DynamicDialog.TAG_NAME,str);
  }
}
