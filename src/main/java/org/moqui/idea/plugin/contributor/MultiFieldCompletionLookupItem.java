package org.moqui.idea.plugin.contributor;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupItem;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.dom.model.Field;
import org.moqui.idea.plugin.util.MyDomUtils;

public class MultiFieldCompletionLookupItem extends LookupItem {
    private Field myField;

    public MultiFieldCompletionLookupItem(@NotNull Field field){
        super(field,MyDomUtils.getValueOrEmptyString(field.getName()));
        myField = field;
    }

    @NotNull
    @Override
    public Object getObject() {
        return myField;
    }

    @Override
    public @NotNull String getLookupString() {
        return MyDomUtils.getValueOrEmptyString(myField.getName());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void renderElement(@NotNull LookupElementPresentation presentation) {
        super.renderElement(presentation);
    }

    @Override
    public void handleInsert(@NotNull InsertionContext context) {
        if (context.getCompletionChar() == ',') {
            AutoPopupController.getInstance(context.getProject()).scheduleAutoPopup(context.getEditor());
        }
    }
}
