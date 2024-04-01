package org.moqui.idea.plugin.editor;

import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.ui.DomFileEditor;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Screen;
import org.moqui.idea.plugin.editor.ui.ScreenEditorWindow;
import org.moqui.idea.plugin.util.ScreenUtils;

public class ScreenEditorProvider implements FileEditorProvider, DumbAware {
    @NonNls
    private static final String EDITOR_TYPE_ID = "moqui.screen.editor";
    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile psiFile = psiManager.findFile(file);
        if(psiFile == null) return false;

        return ScreenUtils.isScreenFile(psiFile);
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        PsiManager psiManager = PsiManager.getInstance(project);
        PsiFile psiFile = psiManager.findFile(file);
        if(psiFile == null) return null;
        if(!(psiFile instanceof XmlFile xmlFile)) return null;
        DomManager manager = DomManager.getDomManager(project);
        Screen screen = manager.getFileElement(xmlFile,Screen.class).getRootElement();
        DomFileEditor domFileEditor = new DomFileEditor(screen,"Screen Form Builder",new ScreenEditorWindow(screen));

        return domFileEditor;

    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }

    @Override
    public void disposeEditor(@NotNull FileEditor editor) {
        Disposer.dispose(editor);
//        FileEditorProvider.super.disposeEditor(editor);
    }

    @Override
    public @NotNull FileEditorState readState(@NotNull Element sourceElement, @NotNull Project project, @NotNull VirtualFile file) {
//        return FileEditorProvider.super.readState(sourceElement, project, file);
        return new FileEditorState() {
            @Override
            public boolean canBeMergedWith(@NotNull FileEditorState otherState, @NotNull FileEditorStateLevel level) {
                return false;
            }
        };
    }

    @Override
    public void writeState(@NotNull FileEditorState state, @NotNull Project project, @NotNull Element targetElement) {
        FileEditorProvider.super.writeState(state, project, targetElement);
        // things like cursor position etc

    }
}
