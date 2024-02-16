package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.ServiceCall;

public class ServiceCallPresentationProvider extends PresentationProvider<ServiceCall> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(ServiceCall coordinates) {
    String serviceCallName =StringUtil.notNullize(coordinates.getName().getStringValue(), UNKNOWN);

    return serviceCallName;
  }
}
