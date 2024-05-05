package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Component;
import org.moqui.idea.plugin.dom.model.Entity;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;

public class ComponentPresentationProvider extends PresentationProvider<Component> {

  private static final String UNKNOWN = "<N/A>";

//  @Override
//  public @Nullable @Nls(capitalization = Nls.Capitalization.Title) String getTypeName(Entity entity) {
//    return entity.getEntityName().getStringValue();
//  }

  @Nullable
  @Override
  public String getName(Component coordinates) {
    String name = MyDomUtils.getXmlAttributeValueString(coordinates.getName().getXmlAttributeValue())
            .orElse(MyStringUtils.EMPTY_STRING)
            + ':'
            + StringUtil.notNullize(coordinates.getVersion().getStringValue(), UNKNOWN);
    return MyStringUtils.formatPresentationName(Component.TAG_NAME,name);
  }
}
