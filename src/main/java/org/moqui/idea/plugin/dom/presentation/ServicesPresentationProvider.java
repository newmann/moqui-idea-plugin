package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Optional;

public class ServicesPresentationProvider extends PresentationProvider<Services> {

  @Nullable
  @Override
  public String getName(Services coordinates) {
    Optional<String> result = ServiceUtils.extractClassNameFromPath(coordinates.getXmlTag().getContainingFile().getVirtualFile().getPath());
    if(result.isPresent()) {
      return result.get();
    }else {
      return MyStringUtils.UNKNOWN;
    }
  }
}
