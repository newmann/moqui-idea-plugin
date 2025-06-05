package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.ContainerDialog;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class ContainerDialogPresentationProvider extends PresentationProvider<ContainerDialog> {

  @Nullable
  @Override
  public String getName(ContainerDialog coordinates) {
    String str = MyDomUtils.getXmlAttributeValueString(coordinates.getId().getXmlAttributeValue())
            .orElse(MyStringUtils.UNKNOWN);

    return MyStringUtils.formatPresentationName(ContainerDialog.TAG_NAME,str);
  }
}
