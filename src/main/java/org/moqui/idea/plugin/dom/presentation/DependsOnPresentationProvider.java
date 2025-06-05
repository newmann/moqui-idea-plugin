package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.DependsOn;
import org.moqui.idea.plugin.util.MyStringUtils;

public class DependsOnPresentationProvider extends PresentationProvider<DependsOn> {



//  @Override
//  public @Nullable Icon getIcon(Field field) {
//    if(field.getIsPk().exists()) {
//      return MoquiIcons.PrimaryKeyField;
//    }else {
//      return super.getIcon(field);
//    }
//
//  }

  @Nullable
  @Override
  public String getName(DependsOn coordinates) {
    String showTitle;
    String field;
    field = StringUtil.notNullize(coordinates.getField().getStringValue(), MyStringUtils.UNKNOWN);
    if(field.equals(MyStringUtils.UNKNOWN)) {
      showTitle = StringUtil.notNullize(coordinates.getName().getStringValue(), MyStringUtils.UNKNOWN)
              +":"
              +StringUtil.notNullize(coordinates.getVersion().getStringValue(), MyStringUtils.UNKNOWN);

    }else {
      showTitle = field
              +"-"
              +StringUtil.notNullize(coordinates.getParameter().getStringValue(), MyStringUtils.UNKNOWN);

    }

    return MyStringUtils.formatPresentationName(DependsOn.TAG_NAME, showTitle);

  }
}
