package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import org.jetbrains.annotations.NotNull;

public interface Action extends DomElement {
    public static final String TAG_NAME = "action";

    @NotNull
    @SubTag(Dialog.TAG_NAME)
    Dialog getDialog();
    @NotNull
    @SubTag(FormSingle.TAG_NAME)
    FormSingle getFormSingle();
}
