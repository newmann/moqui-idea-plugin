package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.ServiceCall;
import org.moqui.idea.plugin.util.MyStringUtils;

public class ServiceCallPresentationProvider extends PresentationProvider<ServiceCall> {
  @Nullable
  @Override
  public String getName(ServiceCall coordinates) {
    String serviceCallName =StringUtil.notNullize(coordinates.getName().getStringValue(), MyStringUtils.UNKNOWN);

    return serviceCallName;
  }
}
