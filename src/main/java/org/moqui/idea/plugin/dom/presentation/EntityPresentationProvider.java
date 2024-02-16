package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class EntityPresentationProvider extends PresentationProvider<Entity> {

  private static final String UNKNOWN = "<N/A>";

//  @Override
//  public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getTypeName(Entity entity) {
//    return entity.getEntityName().getStringValue();
//  }

  @Nullable
  @Override
  public String getName(Entity coordinates) {
    String name = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(MyStringUtils.EMPTY_STRING);
    if(name.equals(MyStringUtils.EMPTY_STRING)) {
      String entityName = StringUtil.notNullize(coordinates.getPackage().getStringValue(), UNKNOWN)
              + '.'
              + StringUtil.notNullize(coordinates.getEntityName().getStringValue(), UNKNOWN);
      if (coordinates.getShortAlias().exists()) {
        entityName = entityName + "(" + coordinates.getShortAlias().getStringValue() + ")";
      }

      return entityName;
    }else {
      return MyStringUtils.formatPresentationName(Entity.TAG_NAME,name);
    }
  }
}
