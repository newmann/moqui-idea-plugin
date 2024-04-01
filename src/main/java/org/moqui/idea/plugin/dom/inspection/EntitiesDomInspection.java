package org.moqui.idea.plugin.dom.inspection;

import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.highlighting.BasicDomElementsInspection;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.moqui.idea.plugin.MyBundle;
import org.moqui.idea.plugin.dom.model.Entities;
@Deprecated
public class EntitiesDomInspection extends BasicDomElementsInspection<Entities> {
    public EntitiesDomInspection(@NotNull Class<? extends Entities> domClass, Class<? extends Entities>... additionalClasses) {
        super(domClass, additionalClasses);
    }
    public EntitiesDomInspection(){
        super(Entities.class);

    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Sentence) @NotNull String getGroupDisplayName() {
        return MyBundle.message("inspection.group");
    }

    @Override
    public @NonNls @NotNull String getShortName() {
        return MyBundle.message("inspection.entities");
    }

    @Override
    protected boolean shouldCheckResolveProblems(GenericDomValue<?> value) {
        return true;
    }
}
