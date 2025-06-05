package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.moqui.idea.plugin.MyIcons;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Field;
import org.moqui.idea.plugin.util.MyStringUtils;

import javax.swing.*;

public class FieldPresentationProvider extends PresentationProvider<Field> {
  @Override
  public @Nullable Icon getIcon(Field field) {
    if(field.getIsPk().exists()) {
      return MyIcons.PrimaryKeyField;
    }else {
      return super.getIcon(field);
    }

  }

  @Nullable
  @Override
  public String getName(Field coordinates) {
    String fieldName;
    if(coordinates.getColumnName().exists()){
      fieldName = StringUtil.notNullize(coordinates.getColumnName().getStringValue(), MyStringUtils.UNKNOWN);
    }else {
      fieldName = StringUtil.notNullize(coordinates.getName().getStringValue(), MyStringUtils.UNKNOWN);
    }
//    fieldName = fieldName
//            + '-'
//            + StringUtil.notNullize(coordinates.getType().getStringValue(), UNKNOWN);
//    if(coordinates.getIsPk().exists()){
//      fieldName = fieldName + "(PK)";
//    }
    if(coordinates.getEnableAuditLog().exists()){
      fieldName = fieldName + "-(A)";
    }
    if(coordinates.getEnableLocalization().exists()){
      fieldName = fieldName + "-(L)";
    }

    return MyStringUtils.formatPresentationName(Field.TAG_NAME,fieldName);

  }
}
