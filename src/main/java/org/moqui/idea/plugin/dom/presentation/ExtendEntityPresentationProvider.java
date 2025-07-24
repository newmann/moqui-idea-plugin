package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.ExtendEntity;
import org.moqui.idea.plugin.util.MyStringUtils;

public class ExtendEntityPresentationProvider extends PresentationProvider<ExtendEntity> {

//  @Override
//  public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getTypeName(Entity entity) {
//    return entity.getEntityName().getStringValue();
//  }

  @Nullable
  @Override
  public String getName(ExtendEntity coordinates) {
    String entityName = StringUtil.notNullize(coordinates.getPackage().getStringValue(), MyStringUtils.UNKNOWN)
            + '.'
            + StringUtil.notNullize(coordinates.getEntityName().getStringValue(), MyStringUtils.UNKNOWN);

      return MyStringUtils.formatPresentationName(ExtendEntity.TAG_NAME,entityName);

  }
}
