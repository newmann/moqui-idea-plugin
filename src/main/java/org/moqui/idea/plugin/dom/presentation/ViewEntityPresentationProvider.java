package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.ViewEntity;

public class ViewEntityPresentationProvider extends PresentationProvider<ViewEntity> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(ViewEntity coordinates) {
    String entityName =StringUtil.notNullize(coordinates.getPackage().getStringValue(), UNKNOWN)
            + '.'
            + StringUtil.notNullize(coordinates.getEntityName().getStringValue(), UNKNOWN);
    return entityName;
  }
}
