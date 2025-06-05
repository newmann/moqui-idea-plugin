package org.moqui.idea.plugin.provider;


import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.FoldRegion;
import com.intellij.openapi.editor.FoldingModel;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Function;
import org.moqui.idea.plugin.MyIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.moqui.idea.plugin.dom.model.FormList;
import org.moqui.idea.plugin.dom.model.FormSingle;
import org.moqui.idea.plugin.util.ScreenUtils;

import java.awt.event.MouseEvent;
import java.util.List;

public class FormFoldingLineMarkerProvider implements  LineMarkerProvider{

  public static Function<? super PsiElement, String> createTooltipSupplier() {
    // 使用 lambda 表达式创建 Supplier
    return (tag) -> "Click to toggle Form content visibility.";
  }

  public @Nullable LineMarkerInfo<PsiElement> getLineMarkerInfo(@NotNull PsiElement element) {
    //只有xmlTag才进行处理
    if(!(element instanceof XmlTag xmlTag))  return null;
    //只有ScreenFile才处理
    if(!ScreenUtils.isScreenFile(element.getContainingFile())) return null;
    List<String> markerTagList = List.of(FormSingle.TAG_NAME, FormList.TAG_NAME);
    if(markerTagList.contains(xmlTag.getName())) {
      GutterIconNavigationHandler<PsiElement> handler = new XmlTagNavigationHandler();
      PsiElement firstChild = xmlTag.getFirstChild();
      return new LineMarkerInfo<PsiElement>(firstChild,
              xmlTag.getTextRange(),
              MyIcons.ScreenTag,
              createTooltipSupplier(),
              handler,
              GutterIconRenderer.Alignment.RIGHT,
              ()->"Fold/Unfold Form content"
              );
    }
    return null;
  }



  public static class XmlTagNavigationHandler implements GutterIconNavigationHandler<PsiElement> {

    @Override
    public void navigate(MouseEvent e, PsiElement elt) {
      // 获取标签的开始和结束位置
      if(!(elt.getParent() instanceof XmlTag formTag)) return;
      Editor editor = FileEditorManager.getInstance(elt.getProject()).getSelectedTextEditor();
      if(editor == null) return;
      FoldingModel foldingModel = editor.getFoldingModel();

      TextRange range = formTag.getTextRange();
      FoldRegion region = foldingModel.getFoldRegion(range.getStartOffset(), range.getEndOffset());

      if(region != null) {
        foldingModel.runBatchFoldingOperation(()->{
            region.setExpanded(!region.isExpanded());
        });
      }
    }

  }
}
