package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.ViewEntity;
import org.moqui.idea.plugin.util.MyStringUtils;

public class ViewEntityPresentationProvider extends PresentationProvider<ViewEntity> {

  @Nullable
  @Override
  public String getName(ViewEntity coordinates) {
    String entityName =StringUtil.notNullize(coordinates.getPackage().getStringValue(), MyStringUtils.UNKNOWN)
            + '.'
            + StringUtil.notNullize(coordinates.getEntityName().getStringValue(), MyStringUtils.UNKNOWN);
    return entityName;
  }
}
