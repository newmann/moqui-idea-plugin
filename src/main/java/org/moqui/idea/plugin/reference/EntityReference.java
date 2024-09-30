// Copyright 2000-2023 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package org.moqui.idea.plugin.reference;

import icons.MoquiIcons;
import kotlinx.html.A;
import org.moqui.idea.plugin.util.EntityUtils;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.xml.XmlElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class EntityReference extends PsiReferenceBase<PsiElement> {

  /**
   * 包含包名的实体名
   */
  private final String entityFullName;
  private final EntityUtils.EntityDescriptor entityDescriptor;


  EntityReference(@NotNull PsiElement element, TextRange textRange) {
    super(element, textRange);
    entityFullName = element.getText().substring(textRange.getStartOffset(), textRange.getEndOffset());
    entityDescriptor = EntityUtils.getEntityDescriptorFromFullName(entityFullName);
  }


  public  @NotNull ResolveResult[] multiResolve(boolean incompleteCode) {
//    Project project = myElement.getProject();
////    final List<SimpleProperty> properties = SimpleUtil.findProperties(project, key);
//    Optional<XmlElement[]> element = EntityUtils.findEntityByFullName(project,entityFullName);
//    List<ResolveResult> results = new ArrayList<>();
//    if (element.isEmpty()) {
//      return results.toArray(new ResolveResult[0]);
//    }else {
//      for (XmlElement item : element.get()) {
//        results.add(new PsiElementResolveResult(item));
//      }
//      ResolveResult[] resultArray = new ResolveResult[results.size()];
//      return results.toArray(resultArray);
//    }
    return new ResolveResult[0];
  }

  @Nullable
  @Override
  public PsiElement resolve() {
    ResolveResult[] resolveResults = multiResolve(false);
    return resolveResults.length == 1 ? resolveResults[0].getElement() : null;
  }

  @Override
  public  @NotNull  Object[] getVariants() {
    Project project = myElement.getProject();
////    List<SimpleProperty> properties = SimpleUtil.findProperties(project);
//    Optional<XmlElement[]> element = EntityUtils.findEntityByEntityDescriptor(project,entityDescriptor);
    List<LookupElement> variants = new ArrayList<>();
    EntityUtils.getAllEntityFullNameCollection(project).forEach(
            item->{
        variants.add(LookupElementBuilder
                .create(item).withIcon(MoquiIcons.EntityTag)
                .withTypeText(item));

            }
    );

//    if(!element.isEmpty()) {
//      for (XmlElement item : element.get()) {
//        variants.add(LookupElementBuilder
//                .create(item).withIcon(AllIcons.Ide.Gift)
//                .withTypeText(item.getText()));
//
//      }
//
//    }
////    for (final SimpleProperty property : properties) {
////      if (property.getKey() != null && property.getKey().length() > 0) {
////        variants.add(LookupElementBuilder
////            .create(property).withIcon(SimpleIcons.FILE)
////            .withTypeText(property.getContainingFile().getName())
////        );
////      }
////    }
    return variants.toArray();
  }

}
