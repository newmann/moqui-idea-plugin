package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Optional;

public class ServicesPresentationProvider extends PresentationProvider<Services> {

  private static final String UNKNOWN = "<N/A>";

  @Nullable
  @Override
  public String getName(Services coordinates) {
    Optional<String> result = ServiceUtils.extractClassNameFromPath(coordinates.getXmlTag().getContainingFile().getVirtualFile().getPath());
    if(result.isPresent()) {
      return result.get();
    }else {
      return UNKNOWN;
    }
  }
}
