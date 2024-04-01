package org.moqui.idea.plugin.provider;

import org.moqui.idea.plugin.service.FindRelatedItemService;
import org.moqui.idea.plugin.service.FindRelatedItemServiceFactory;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;


import java.util.Collection;
import java.util.List;

public class XmlFileLineMarkerProvider extends RelatedItemLineMarkerProvider {

  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
    FindRelatedItemService findRelatedItemService = FindRelatedItemServiceFactory.getFindRelatedItemService(element);

    List<PsiElement> resultList = findRelatedItemService.findRelatedItem(element);
    if (!resultList.isEmpty()) {
      NavigationGutterIconBuilder<PsiElement> builder =
          NavigationGutterIconBuilder.create(findRelatedItemService.getNagavitorToIcon())
              .setTargets(resultList)
              .setTooltipText(findRelatedItemService.getToolTips());
      result.add(builder.createLineMarkerInfo(element));
    }

//    //只有xmlTag才进行处理
//    if(!(element instanceof XmlTag))  return;
//
//    //获取rootTag，tagName，判断是否进行处理以及如何处理
//    Optional<String> optRootTagName = DomUtils.getRootTagNameByXmlToken((XmlToken) element);
//    if(optRootTagName.isEmpty()) return;
//    final String rootTagName = optRootTagName.get();
//
//    final String tagName = ((XmlTag)element).getName();





  }

}
