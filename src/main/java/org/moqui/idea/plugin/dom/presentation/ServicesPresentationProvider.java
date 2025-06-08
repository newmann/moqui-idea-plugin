package org.moqui.idea.plugin.dom.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.Services;
import org.moqui.idea.plugin.util.MyDomUtils;
import org.moqui.idea.plugin.util.MyStringUtils;
import org.moqui.idea.plugin.util.ServiceUtils;

import java.util.Optional;

public class ServicesPresentationProvider extends PresentationProvider<Services> {

  @Nullable
  @Override
  public String getName(Services coordinates) {
    if(coordinates == null || coordinates.getXmlTag() == null) return MyStringUtils.UNKNOWN;

    Optional<String> pathOptional = MyDomUtils.getFilePathByPsiElement(coordinates.getXmlTag());
    if(pathOptional.isEmpty())  return MyStringUtils.UNKNOWN;

    Optional<String> result = ServiceUtils.extractClassNameFromPath(pathOptional.get());
    return result.orElse(MyStringUtils.UNKNOWN);

  }
}
